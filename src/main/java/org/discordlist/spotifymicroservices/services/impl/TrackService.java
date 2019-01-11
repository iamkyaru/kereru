package org.discordlist.spotifymicroservices.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.requests.AbstractRequest;
import org.discordlist.spotifymicroservices.services.IService;

import java.io.IOException;
import java.util.*;

public class TrackService extends AbstractRequest implements IService<Track> {

    private final Map<String, Track> trackMap;

    public TrackService() {
        super();
        this.trackMap = new HashMap<>();
    }

    @Override
    public void add(Track track) {
        if (track != null)
            this.trackMap.put(track.getId(), track);
    }

    @Override
    public Collection<Track> getCachedValues() {
        return trackMap.values();
    }

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

    public Track makeTrack(JsonObject jsonObject) {
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

    @Override
    public Track edit(Track track) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(String id) {
        if (id != null)
            this.trackMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.trackMap.containsKey(id);
        return false;
    }
}
