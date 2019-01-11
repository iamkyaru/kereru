package org.discordlist.spotifymicroservices.cache;

import redis.clients.jedis.Jedis;

public class Cache<T> {

    private final Class<T> typeParameterClass;
    private final String path;
    private final RedisSession redis;

    public Cache(Class<T> typeParameterClass, String path, RedisSession redis) {
        this.typeParameterClass = typeParameterClass;
        this.path = path;
        this.redis = redis;
    }

    public T get(String id) {
        try (Jedis jedis = redis.pool().getResource()) {
            String res = jedis.get(path + "." + id);
            System.out.println(res);
            return null;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
