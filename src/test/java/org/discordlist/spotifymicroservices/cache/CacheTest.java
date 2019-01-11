package org.discordlist.spotifymicroservices.cache;

import org.discordlist.spotifymicroservices.entities.Track;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheTest {

    private static RedisSession redisSession;
    private static Cache<Track> trackCache;

    @BeforeAll
    static void setUp() {
        redisSession = new RedisSession("localhost", 6379);
        trackCache = new Cache<Track>(Track.class, "spotify.tracks", redisSession) {
            @Override
            public Track fetchEntity(String id) {
                return new Track("123", null, null, null, 0, null, null, false, false);
            }
        };
    }

    @Test
    void get() {
        Track track = trackCache.get("123");
        System.out.println(1);
        Assertions.assertNotNull(track);
    }
}