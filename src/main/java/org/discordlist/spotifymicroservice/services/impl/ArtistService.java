package org.discordlist.spotifymicroservice.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.cache.Cache;
import org.discordlist.spotifymicroservice.cache.RedisSession;
import org.discordlist.spotifymicroservice.entities.Artist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.requests.AbstractRequest;
import org.discordlist.spotifymicroservice.services.IService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Log4j2
public class ArtistService extends AbstractRequest implements IService<Artist> {

    @Getter
    private final Cache<Artist> cache;

    public ArtistService(RedisSession redisSession) {
        super();
        this.cache = new Cache<Artist>(Artist.class, "spotify.artists", redisSession) {
            @Override
            public Artist fetch(@NonNull String id) {
                return ArtistService.this.get(id);
            }
        };
    }

    /**
     * Returns a {@link Collection<Artist>} of all cached {@link Artist} values.
     *
     * @return all cached values.
     */
    @Override
    public Collection<Artist> getCachedValues() {
        return this.cache.all();
    }

    /**
     * Returns the wanted {@link Artist}, which can be received from the given id parameter.
     *
     * @param id the {@link Artist} id
     * @return the {@link Artist} which should be returned from the given id or null if the id is not from an actual {@link Track}.
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
                log.error("Could not fetch Artist", e);
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
        return Artist.builder()
                .id(id)
                .name(name)
                .url(url)
                .href(href)
                .uri(uri)
                .topTracks(tracks).build();
    }

    /**
     * Returns a {@link List<Track>} of the most popular songs from the {@link Artist}.
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
                    Track track = SpotifyMicroservice.instance().trackService().makeTrack(jsonObject);
                    tracks.add(track);
                });
            }
        } catch (IOException e) {
            log.error(String.format("Failed to fetch top-tracks from artist with id: %s", artistId), e);
        }
        return tracks;
    }
}