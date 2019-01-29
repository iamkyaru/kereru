package org.discordlist.spotifymicroservice.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservice.cache.Cache;
import org.discordlist.spotifymicroservice.cache.RedisSession;
import org.discordlist.spotifymicroservice.entities.Artist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.requests.AbstractRequest;
import org.discordlist.spotifymicroservice.services.IService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The Service, which has all the methods to retrieve tracks.
 */
@Log4j2
public class TrackService extends AbstractRequest implements IService<Track> {

    @Getter
    private final Cache<Track> cache;

    public TrackService(RedisSession redisSession) {
        super();
        this.cache = new Cache<Track>(Track.class, "spotify.tracks", redisSession) {
            @Override
            public Track fetch(String id) {
                return TrackService.this.get(id);
            }
        };
    }

    /**
     * Get all cached values.
     *
     * @return all cached values.
     */
    @Override
    public Collection<Track> getCachedValues() {
        return this.cache.all();
    }

    /**
     * Returns the wanted {@link Track}, which can be received from the given id parameter.
     *
     * @param id the id from an {@link Track}
     * @return the {@link Track} which should be returned from the given id or null if the id is not from an actual Track.
     */
    @Override
    public Track get(String id) {
        if (id != null) {
            Request request = new Request.Builder()
                    .url(API_BASE + "/tracks/" + id)
                    .get()
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() != null) {
                    JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    return makeTrack(jsonObject);
                }
            } catch (IOException e) {
                log.error("Could not fetch Track", e);
            }
        }
        return null;
    }

    @Override
    public void add(Track track) {
        this.cache.update(track);
    }

    /**
     * Returns the {@link Track} Entity which is created by the given {@link JsonObject}
     *
     * @param jsonObject the {@link JsonObject} with the values from an Json Response
     * @return the {@link Track} which has been created from the given {@link JsonObject}
     */
    Track makeTrack(JsonObject jsonObject) {
        String id = jsonObject.get("id").getAsString();
        List<Artist> artists = new ArrayList<>();
        jsonObject.get("artists").getAsJsonArray().forEach(jsonElement -> {
            JsonObject artistObject = jsonElement.getAsJsonObject();
            String artistId = artistObject.get("id").getAsString();
            String name = artistObject.get("name").getAsString();
            String href = artistObject.get("href").getAsString();
            String uri = artistObject.get("uri").getAsString();
            String url = artistObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
            artists.add(Artist.builder().id(artistId).name(name).url(url).href(href).uri(uri).topTracks(Collections.emptyList()).build());
        });
        String href = jsonObject.get("href").getAsString();
        String name = jsonObject.get("name").getAsString();
        String uri = jsonObject.get("uri").getAsString();
        String url = jsonObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
        long duration = jsonObject.get("duration_ms").getAsLong();
        boolean local = jsonObject.get("is_local").getAsBoolean();
        boolean explicit = jsonObject.get("explicit").getAsBoolean();
        return Track.builder()
                .id(id)
                .name(name)
                .artists(artists)
                .url(url)
                .durationTimeMillis(duration)
                .href(href)
                .uri(uri)
                .local(local)
                .explicit(explicit)
                .build();
    }
}