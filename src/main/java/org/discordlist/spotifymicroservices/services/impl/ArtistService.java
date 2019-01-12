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
import java.util.concurrent.ConcurrentHashMap;

public class ArtistService extends AbstractRequest implements IService<Artist> {

    private final Map<String, Artist> artistMap;

    public ArtistService() {
        super();
        this.artistMap = new ConcurrentHashMap<>();
    }

    /**
     * Adds an {@link Artist} to the in-memory cache.
     *
     * @param artist the track which should be added.
     */
    @Override
    public void add(Artist artist) {
        if (artist != null)
            this.artistMap.put(artist.getId(), artist);
    }

    /**
     * Returns a {@link Collection<Artist>} of all cached {@link Artist} values.
     *
     * @return all cached values.
     */
    @Override
    public Collection<Artist> getCachedValues() {
        return this.artistMap.values();
    }

    /**
     * Returns the wanted {@link Artist}, which can be received from the given id parameter.
     *
     * @param id the {@link Artist} id
     * @return the {@link Artist} which should be returned from the given id or null if the id is not from an actual Track.
     */
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
//                logger.error("Could not fetch Artist", e);
            }
        }
        return null;
    }

    /**
     * Returns a {@link Artist} object which will be created by an {@link JsonObject}
     *
     * @param jsonObject the needed {@link JsonObject} to create the {@link Artist} object.
     * @return the created {@link Artist} object.
     */
    private Artist makeArtist(JsonObject jsonObject) {
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();
        String href = jsonObject.get("href").getAsString();
        String uri = jsonObject.get("uri").getAsString();
        String url = jsonObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
        List<Track> tracks = getTopTracks(id);
        return new Artist(id, name, url, href, uri, tracks);
    }

    /**
     * Returns a {@link List<Track>} of the most popular songs.
     *
     * @param artistId the {@link Artist} id which is needed for the retrieving of the top tracks.
     * @return a {@link List<Track>} of tracks
     */
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
                    Track track = SpotifyMicroservice.getInstance().trackService().makeTrack(jsonObject);
                    tracks.add(track);
                });
            }
        } catch (IOException e) {
//            logger.error(String.format("Failed to fetch top-tracks from artist with id: %s", artistId), e);
        }
        return tracks;
    }

    /**
     * Not supported.
     */
    @Override
    public Artist edit(Artist artist) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Deletes the given {@link Artist} id from the cache.
     *
     * @param id the {@link Artist} id, which is wanted to be deleted from the in-memory cache.
     */
    @Override
    public void delete(String id) {
        if (id != null)
            this.artistMap.remove(id);
    }

    /**
     * Returns the existence of an Artist from the given {@link Artist} id.
     *
     * @param id the {@link Artist} id
     * @return true, if the id is saved in the {@link java.util.concurrent.ConcurrentMap}, otherwise false.
     */
    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.artistMap.containsKey(id);
        return false;
    }
}