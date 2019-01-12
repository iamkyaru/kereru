package org.discordlist.spotifymicroservices.cache;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Nullable;

@Log4j2
@Accessors(fluent = true)
@Getter
public class RedisSession {

    private final JedisPool pool;

    public RedisSession(String host, int port) {
        this(host, port, null);
    }

    public RedisSession(String host, int port, @Nullable String password) {
        if (password != null && password.isEmpty())
            password = null;
        JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(config, host, port, 0, password);
        log.info("[Redis] Connected to: {}:{}", host, port);
    }
}
