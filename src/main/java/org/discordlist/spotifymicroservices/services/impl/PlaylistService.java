package org.discordlist.spotifymicroservices.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.cache.Cache;
import org.discordlist.spotifymicroservices.cache.RedisSession;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.requests.AbstractRequest;
import org.discordlist.spotifymicroservices.services.IService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class PlaylistService extends AbstractRequest implements IService<Playlist> {

    private final Cache<Playlist> cache;

    public PlaylistService(RedisSession redisSession) {
        super();
        this.cache = new Cache<Playlist>(Playlist.class, "spotify.playlists", redisSession) {

            @Override
            public Playlist fetch(String id) {
                return PlaylistService.this.get(id);
            }
        };

        try {
            FileHandler fileHandler = new FileHandler("output.log");
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.info("Start");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(Playlist playlist) {
        this.cache.update(playlist);
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
//                logger.error("Could not fetch Playlist", e);
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
                .url(url).build();
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
                    e.printStackTrace();
                }
            }

            HttpUrl.Builder httpBuilder = HttpUrl.parse(url).newBuilder()
                    .addQueryParameter("offset", offset)
                    .addQueryParameter("limit", limit);
            Request.Builder builder = new Request.Builder()
                    .url(httpBuilder.build())
                    .get();
            try (Response response = httpClient.newCall(builder.build()).execute()) {
                if (response.body() != null) {
                    jsonPage = new JsonParser().parse(response.body().string()).getAsJsonObject();
                    logger.info(String.valueOf(jsonPage));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (jsonPage.has("next") && jsonPage.get("next") != null);
        JsonArray jsonArray = jsonPage.getAsJsonArray("items");
        jsonArray.forEach(jsonElement -> {
            JsonObject jsonObject = jsonElement.getAsJsonObject().get("track").getAsJsonObject();
            Track track = SpotifyMicroservice.getInstance().trackService().makeTrack(jsonObject);
            tracks.add(track);
        });
        return tracks;
    }

    private String getParamValue(String url, String parameter) throws URISyntaxException {
        List<NameValuePair> queryParams = new URIBuilder(url).getQueryParams();
        return queryParams.stream()
                .filter(param -> param.getName().equalsIgnoreCase(parameter))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse("");
    }

    @Override
    public Playlist edit(Playlist playlist) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(String id) {
        this.cache.delete(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.cache.exist(id);
        return false;
    }

    public Cache<Playlist> getCache() {
        return this.cache;
    }
}
