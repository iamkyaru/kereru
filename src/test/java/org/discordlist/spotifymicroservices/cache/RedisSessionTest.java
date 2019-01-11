package org.discordlist.spotifymicroservices.cache;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

class RedisSessionTest {

    private static RedisSession redisSession;

    @BeforeAll
    static void setup() {
        redisSession = new RedisSession("localhost", 6379);
    }

    @Test
    void pool() {
        try (Jedis jedis = redisSession.pool().getResource()) {
            System.out.println(jedis.ping());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}