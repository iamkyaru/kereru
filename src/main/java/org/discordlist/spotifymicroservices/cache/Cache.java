package org.discordlist.spotifymicroservices.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Cache<T extends Cacheable> {

    private final Gson gson;
    private final Class<T> typeParameterClass;
    private final String path;
    private final RedisSession redis;

    public Cache(Class<T> typeParameterClass, String path, RedisSession redis) {
        this.typeParameterClass = typeParameterClass;
        this.path = path;
        this.redis = redis;
        this.gson = new GsonBuilder().create();
    }

    public void update(Cacheable cacheable) {
        try (Jedis jedis = redis.pool().getResource()) {
            jedis.hset(path, cacheable.getId(), gson.toJson(cacheable));
        }
    }

    public void delete(String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            jedis.del(path + "." + id);
        }
    }

    public List<T> all() {
        try (Jedis jedis = redis.pool().getResource()) {
            return jedis.hgetAll(path).values().stream().map(s -> gson.fromJson(s, typeParameterClass)).collect(Collectors.toList());
        }
    }

    public T get(String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            if (!jedis.exists(path + "." + id))
                return fetchAndCacheEntity(id);
            String res = jedis.hget(path, id);
            return gson.fromJson(res, typeParameterClass);
        }
    }

    private T fetchAndCacheEntity(String id) {
        T entity = fetchEntity(id);
        update(entity);
        return entity;
    }

    public abstract T fetchEntity(String id);
}
