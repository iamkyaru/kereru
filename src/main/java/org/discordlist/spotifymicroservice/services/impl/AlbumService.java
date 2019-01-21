package org.discordlist.spotifymicroservice.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.cache.Cache;
import org.discordlist.spotifymicroservice.cache.RedisSession;
import org.discordlist.spotifymicroservice.entities.Album;
import org.discordlist.spotifymicroservice.entities.Artist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.requests.AbstractRequest;
import org.discordlist.spotifymicroservice.services.IService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Log4j2
public class AlbumService extends AbstractRequest implements IService<Album> {

    @Getter
    private final Cache<Album> cache;

    public AlbumService(RedisSession redisSession) {
        this.cache = new Cache<Album>(Album.class, "spotify.albums", redisSession) {
            @Override
            public Album fetch(@NonNull String id) {
                return AlbumService.this.get(id);
            }
        };
    }

    /**
     * Get all cached values.
     *
     * @return all cached values.
     */
    @Override
    public Collection<Album> getCachedValues() {
        return this.cache.all();
    }

    @Override
    public Album get(String id) {
        if (id != null) {
            Request request = new Request.Builder()
                    .url(API_BASE + "/albums/" + id)
                    .get()
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() != null) {
                    JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    return makeAlbum(jsonObject);
                }
            } catch (IOException e) {
                log.error("Could not fetch Album", e);
            }
        }
        return null;
    }

    private Album makeAlbum(JsonObject jsonObject) {
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();
        List<Artist> artists = new ArrayList<>();
        jsonObject.get("artists").getAsJsonArray().forEach(jsonElement -> {
            JsonObject artistObject = jsonElement.getAsJsonObject();
            String artistId = artistObject.get("id").getAsString();
            String artistName = artistObject.get("name").getAsString();
            String href = artistObject.get("href").getAsString();
            String uri = artistObject.get("uri").getAsString();
            String url = artistObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
            artists.add(Artist.builder()
                    .id(artistId)
                    .name(artistName)
                    .url(url)
                    .href(href)
                    .uri(uri)
                    .build());
        });
        String href = jsonObject.get("href").getAsString();
        String uri = jsonObject.get("uri").getAsString();
        String url = jsonObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
        List<Track> tracks = getTracks(id);
        return Album.builder()
                .id(id)
                .name(name)
                .artists(artists)
                .url(url)
                .href(href)
                .uri(uri)
                .tracks(tracks)
                .build();
    }

    private List<Track> getTracks(String albumId) {
        List<Track> tracks = new ArrayList<>();
        String url = API_BASE + "/albums/" + albumId + "/tracks?market=US";
        JsonObject jsonPage = null;
        do {
            String offset = "0";
            String limit = "50";

            if (jsonPage != null) {
                if (!jsonPage.has("next") || jsonPage.get("next").isJsonNull()) break;
                String nextPageUrl = jsonPage.get("next").getAsString();
                try {
                    offset = getParamValue(nextPageUrl, "offset");
                    limit = getParamValue(nextPageUrl, "limit");
                } catch (URISyntaxException e) {
                    log.error("Unable to get parameter values from url", e);
                }
            }

            HttpUrl.Builder httpBuilder = Objects.requireNonNull(HttpUrl.parse(url)).newBuilder()
                    .addQueryParameter("market", "US")
                    .addQueryParameter("offset", offset)
                    .addQueryParameter("limit", limit);
            Request.Builder builder = new Request.Builder()
                    .url(httpBuilder.build())
                    .get();
            try (Response response = httpClient.newCall(builder.build()).execute()) {
                if (response.body() != null) {
                    jsonPage = new JsonParser().parse(response.body().string()).getAsJsonObject();
                }
            } catch (IOException e) {
                log.error("Could not request for album tracks", e);
            }
        } while (Objects.requireNonNull(jsonPage).has("next") && jsonPage.get("next") != null);
        System.out.println(jsonPage.toString());
        JsonArray jsonArray = jsonPage.getAsJsonArray("items");
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Track track = SpotifyMicroservice.instance().trackService().makeTrack(jsonObject);
            tracks.add(track);
        });
        return tracks;
    }
}
