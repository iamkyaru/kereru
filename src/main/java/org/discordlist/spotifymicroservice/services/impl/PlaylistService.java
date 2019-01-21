package org.discordlist.spotifymicroservice.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.cache.Cache;
import org.discordlist.spotifymicroservice.cache.RedisSession;
import org.discordlist.spotifymicroservice.entities.Playlist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.requests.AbstractRequest;
import org.discordlist.spotifymicroservice.services.IService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Log4j2
public class PlaylistService extends AbstractRequest implements IService<Playlist> {

    @Getter
    private final Cache<Playlist> cache;

    public PlaylistService(RedisSession redisSession) {
        super();
        this.cache = new Cache<Playlist>(Playlist.class, "spotify.playlists", redisSession) {

            @Override
            public Playlist fetch(String id) {
                return PlaylistService.this.get(id);
            }
        };
    }

    @Override
    public Collection<Playlist> getCachedValues() {
        return this.cache.all();
    }

    @Override
    public Playlist get(String id) {
        if (id != null) {
            Request request = new Request.Builder()
                    .url(API_BASE + "/playlists/" + id)
                    .get()
                    .build();
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.body() != null) {
                    JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    return makePlaylist(jsonObject);
                }
            } catch (IOException e) {
                log.error("Could not fetch Playlist", e);
            }
        }
        return null;
    }

    private Playlist makePlaylist(JsonObject jsonObject) {
        String id = jsonObject.get("id").getAsString();
        String name = jsonObject.get("name").getAsString();
        String owner = jsonObject.get("owner").getAsJsonObject().get("id").getAsString();
        String href = jsonObject.get("href").getAsString();
        String uri = jsonObject.get("uri").getAsString();
        String url = jsonObject.get("external_urls").getAsJsonObject().get("spotify").getAsString();
        List<Track> tracks = getTracks(id);
        return Playlist.builder()
                .id(id)
                .name(name)
                .owner(owner)
                .href(href)
                .uri(uri)
                .tracks(tracks)
                .url(url)
                .build();
    }

    private List<Track> getTracks(String playlistId) {
        List<Track> tracks = new ArrayList<>();
        String url = API_BASE + "/playlists/" + playlistId + "/tracks?market=US";
        JsonObject jsonPage = null;
        do {
            String offset = "0";
            String limit = "100";

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
                log.error("Could not request for playlist tracks", e);
            }
            JsonArray jsonArray = Objects.requireNonNull(jsonPage).getAsJsonArray("items");
            jsonArray.forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject().get("track").getAsJsonObject();
                Track track = SpotifyMicroservice.instance().trackService().makeTrack(jsonObject);
                tracks.add(track);
            });
        } while (jsonPage.has("next") && jsonPage.get("next") != null);
        return tracks;
    }
}
