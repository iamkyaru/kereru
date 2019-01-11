package org.discordlist.spotifymicroservices.cache;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisSession {

    private final JedisPool pool;

    public RedisSession(String host, int port) {
        this(host, port, null);
    }

    public RedisSession(String host, int port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(config, host, port, 0, password);
    }

    public JedisPool pool() {
        return pool;
    }
}
