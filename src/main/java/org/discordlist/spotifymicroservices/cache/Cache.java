package org.discordlist.spotifymicroservices.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;

public abstract class Cache<T> {

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

    public T get(String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            if (!jedis.exists(path + "." + id))
                return fetchAndCacheEntity(id);
            String res = jedis.get(path + "." + id);
            return gson.fromJson(res, typeParameterClass);
        }
    }

    private T fetchAndCacheEntity(String id) {
        T entity = fetchEntity(id);
        try (Jedis jedis = redis.pool().getResource()) {
            jedis.set(path + "." + id, gson.toJson(entity));
        }
        return entity;
    }

    public abstract T fetchEntity(String id);
}
