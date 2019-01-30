package org.discordlist.spotifymicroservice.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.Jedis;

import javax.annotation.CheckReturnValue;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public abstract class Cache<T extends Cacheable> {

    private final Gson gson;
    private final Class<T> typeParameterClass;
    private final String path;
    private final RedisSession redis;

    /**
     * Creates a cache object.
     *
     * @param typeParameterClass the {@link Class} of the object
     * @param path the redis path where the cache should save the objects
     * @param redis the {@link RedisSession}
     */
    public Cache(@NonNull Class<T> typeParameterClass, @NonNull String path, @NonNull RedisSession redis) {
        this.typeParameterClass = typeParameterClass;
        this.path = path;
        this.redis = redis;
        this.gson = new GsonBuilder().create();
    }

    /**
     * Fetches an entry by id from redis.
     * If there was no entry it will call {@link Cache#fetch(String)}
     *
     * @param id the id of the entry
     * @return the <strong>possible null</strong> entry
     */
    @CheckReturnValue
    public T get(@NonNull String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            if (!exist(id))
                return fetchAndCache(id);
            log.debug("[Cache] ({}) Fetched `{}`", path, id);
            return gson.fromJson(jedis.hget(path, id), typeParameterClass);
        }
    }

    /**
     * Updates or adds an entry in redis.
     *
     * @param cacheable the object you want to save
     */
    public void update(@NonNull Cacheable cacheable) {
        try (Jedis jedis = redis.pool().getResource()) {
            jedis.hset(path, cacheable.id(), gson.toJson(cacheable));
            log.debug("[Cache] ({}) Updated `{}`", path, cacheable.id());
        }
    }

    /**
     * Deletes an entry from redis by id.
     *
     * @param id the id of the entry
     */
    public void delete(@NonNull String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            jedis.hdel(path, id);
            log.debug("[Cache] ({}) Deleted `{}`", path, id);
        }
    }

    /**
     * Checks if an entry exists in redis.
     *
     * @param id the id of the entry
     * @return true if the entry exists
     */
    public boolean exist(@NonNull String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            return jedis.hexists(path, id);
        }
    }

    /**
     * Fetches all entries from redis and returns it as list.
     *
     * @return all entries from this cache
     */
    public List<T> all() {
        try (Jedis jedis = redis.pool().getResource()) {
            log.debug("[Cache] ({}) Fetched all.", path);
            return jedis.hgetAll(path).values().stream().map(s -> gson.fromJson(s, typeParameterClass)).collect(Collectors.toList());
        }
    }

    /**
     * Clears the cache.
     */
    public void clear() {
        try (Jedis jedis = redis.pool().getResource()) {
            jedis.del(path);
            log.debug("[Cache] ({}) Cleared.", path);
        }
    }

    private T fetchAndCache(@NonNull String id) {
        T entity = fetch(id);
        if (entity == null)
            return null;
        update(entity);
        return entity;
    }

    /**
     * Fetches an object if it was requested and not cached yet.
     *
     * @param id the id of the requested object
     * @return the fetched object
     */
    @CheckReturnValue
    public abstract T fetch(@NonNull String id);
}
