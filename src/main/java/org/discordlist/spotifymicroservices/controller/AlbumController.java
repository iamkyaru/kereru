package org.discordlist.spotifymicroservices.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Album;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.IService;
import spark.Route;

@Log4j2
public class AlbumController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final IService<Album> service = SpotifyMicroservice.getInstance().albumService();

    public static final Route POST_ALBUM = (request, response) -> {
        if (request.body() == null || request.body().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Empty request body"));
        Album album = GSON.fromJson(request.body(), Album.class);
        if (album.getId() == null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album not found"));
        service.add(album);
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS));
    };

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
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id).getTracks())));
    };

    public static final Route GET_ALBUM_TRACK = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id)) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album does not exist"));
        }
        String trackId = request.params(":trackId");
        Album album = service.get(id);
        Track track = album.getTracks().stream().filter(t -> t.getId().equals(trackId)).findFirst().orElse(null);
        if (track == null || album.getTracks().stream().noneMatch(t -> t.getId().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };

    public static final Route PUT_ALBUM = (request, response) -> {
        Album album = GSON.fromJson(request.body(), Album.class);
        if (album.getId() == null || album.getId().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album not found"));
        Album editedAlbum = service.edit(album);
        if (editedAlbum != null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(editedAlbum)));
        else
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Album could not be edited."));
    };

    public static final Route DELETE_ALBUM = (request, response) -> {
        service.delete(request.params(":id"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, "Album deleted"));};

    public static final Route OPTIONS_ALBUM = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
            (service.exists(request.params(":id")) ? "Album does exist" : "Album does not exist")));
}