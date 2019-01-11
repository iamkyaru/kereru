package org.discordlist.spotifymicroservices.services.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Request;
import okhttp3.Response;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.requests.AbstractRequest;
import org.discordlist.spotifymicroservices.services.IService;
import org.json.JSONObject;

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
        return new Artist(id, name, url, href, uri, Collections.emptyList());
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
