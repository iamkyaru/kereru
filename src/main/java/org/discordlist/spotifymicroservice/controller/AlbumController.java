package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.entities.Album;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.IService;
import spark.Route;

@Log4j2
public class AlbumController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final IService<Album> service = SpotifyMicroservice.instance().albumService();

    public static final Route GET_ALBUMS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Route GET_ALBUM = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id))));
    };

    public static final Route GET_ALBUM_TRACKS = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id).tracks())));
    };

    public static final Route GET_ALBUM_TRACK = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id)) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album does not exist"));
        }
        String trackId = request.params(":trackId");
        Album album = service.get(id);
        Track track = album.tracks().stream().filter(t -> t.id().equals(trackId)).findFirst().orElse(null);
        if (track == null || album.tracks().stream().noneMatch(t -> t.id().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}