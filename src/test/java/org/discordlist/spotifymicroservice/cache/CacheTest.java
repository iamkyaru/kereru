package org.discordlist.spotifymicroservice.cache;

import org.discordlist.spotifymicroservice.entities.Track;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CacheTest {

    private static Cache<Track> trackCache;

    @BeforeAll
    static void setUp() {
        RedisSession redisSession = new RedisSession("localhost", 6379);
        trackCache = new Cache<Track>(Track.class, "spotify.tracks", redisSession) {
            @Override
            public Track fetch(String id) {
                return Track.builder().id("123").build();
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