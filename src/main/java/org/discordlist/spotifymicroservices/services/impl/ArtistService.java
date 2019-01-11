package org.discordlist.spotifymicroservices.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.requests.AbstractRequest;
import org.discordlist.spotifymicroservices.services.IService;

import java.io.IOException;
import java.util.*;

public class ArtistService extends AbstractRequest implements IService<Artist> {

    private final Map<String, Artist> artistMap;

    public ArtistService() {
        super();
        this.artistMap = new HashMap<>();
    }

    @Override
    public void add(Artist artist) {
        if (artist != null)
            this.artistMap.put(artist.getId(), artist);
    }

    @Override
    public Collection<Artist> getCachedValues() {
        return this.artistMap.values();
    }

    @Override
    public Artist get(String id) {
        if (id != null) {
            Request request = new Request.Builder()
                    .url(API_BASE + "/artists/" + id)
                    .get()
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() != null) {
                    JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    return makeArtist(jsonObject);
                }
            } catch (IOException e) {
                logger.error("Could not fetch Artist", e);
            }
        }
        return null;
    }

    private Artist makeArtist(JsonObject jsonObject) {
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();
        String href = jsonObject.get("href").getAsString();
        String uri = jsonObject.get("uri").getAsString();
        String url = jsonObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
        List<Track> tracks = getTopTracks(id);
        return new Artist(id, name, url, href, uri, tracks);
    }

    private List<Track> getTopTracks(String artistId) {
        List<Track> tracks = new ArrayList<>();
        Request.Builder builder = new Request.Builder()
                .url(API_BASE + "/artists/" + artistId + "/top-tracks?country=US")
                .get();
        try (Response response = httpClient.newCall(builder.build()).execute()) {
            if (response.body() != null) {
                JsonObject rootObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                JsonArray jsonArray = rootObject.getAsJsonArray("tracks");
                jsonArray.forEach(jsonElement -> {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Track track = SpotifyMicroservice.getInstance().getTrackService().makeTrack(jsonObject);
                    tracks.add(track);
                });
            }
        } catch (IOException e) {
            logger.error(String.format("Failed to fetch top-tracks from artist with id: %s", artistId), e);
        }
        return tracks;
    }

    @Override
    public Artist edit(Artist artist) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(String id) {
        if (id != null)
            this.artistMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.artistMap.containsKey(id);
        return false;
    }
}
