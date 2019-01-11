package org.discordlist.spotifymicroservices.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservices.cache.Cache;
import org.discordlist.spotifymicroservices.cache.RedisSession;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.requests.AbstractRequest;
import org.discordlist.spotifymicroservices.services.IService;

import java.io.IOException;
import java.util.*;

/**
 * The Service, which has all the methods to retrieve tracks.
 */
public class TrackService extends AbstractRequest implements IService<Track> {

    private Cache<Track> cache;

    public TrackService(RedisSession redisSession) {
        super();
        this.cache = new Cache<Track>(Track.class, "spotify.tracks", redisSession) {
            @Override
            public Track fetchEntity(String id) {
                return TrackService.this.get(id);
            }
        };
    }

    /**
     * Add an {@link Track} to the in-memory cache.
     *
     * @param track the track which should be added.
     */
    @Override
    public void add(Track track) {
        this.cache.update(track);
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
                logger.error("Could not fetch Track", e);
            }
        }
        return null;
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
            artists.add(new Artist(artistId, name, url, href, uri, Collections.emptyList()));
        });
        String href = jsonObject.get("href").getAsString();
        String name = jsonObject.get("name").getAsString();
        String uri = jsonObject.get("uri").getAsString();
        String url = jsonObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
        long duration = jsonObject.get("duration_ms").getAsLong();
        boolean local = jsonObject.get("is_local").getAsBoolean();
        boolean explicit = jsonObject.get("explicit").getAsBoolean();
        return new Track(id, name, artists, url, duration, href, uri, local, explicit);
    }

    /**
     * Not supported.
     */
    @Override
    public Track edit(Track track) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Deletes the given {@link Track} id from the cache.
     *
     * @param id the {@link Track} id, which is wanted to be deleted from the in-memory cache.
     */
    @Override
    public void delete(String id) {
        this.cache.delete(id);
    }

    /**
     * Returns the existence of an Track from the given {@link Track} id.
     *
     * @param id the {@link Track} id
     * @return true, if the id is saved in the {@link java.util.concurrent.ConcurrentMap}, otherwise false.
     */
    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.cache.exist(id);
        return false;
    }

    public Cache<Track> getCache() {
        return this.cache;
    }
}