package org.discordlist.spotifymicroservices.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.Service;
import spark.Route;

public class PlaylistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Service<Playlist> service = SpotifyMicroservice.getPlaylistService();

    public static final Route POST_PLAYLIST = (request, response) -> {
        if (request.body() == null || request.body().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Empty request body"));
        Playlist playlist = GSON.fromJson(request.body(), Playlist.class);
        if (playlist.getId() == null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist not found"));
        service.add(playlist);
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS));
    };

    public static final Route GET_PLAYLISTS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCollection())));

    public static final Route GET_PLAYLIST = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id))));
    };

    public static final Route GET_PLAYLIST_TRACKS = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id).getTracks())));
    };

    public static final Route GET_PLAYLIST_TRACK = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id)) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist does not exist"));
        }
        String trackId = request.params(":trackId");
        Playlist playlist = service.get(id);
        Track track = playlist.getTracks().stream().filter(t -> t.getId().equals(trackId)).findFirst().orElse(null);
        if (track == null || playlist.getTracks().stream().noneMatch(t -> t.getId().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };

    public static final Route PUT_PLAYLIST = (request, response) -> {
        Playlist playlist = GSON.fromJson(request.body(), Playlist.class);
        if (playlist.getId() == null || playlist.getId().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist not found"));
        Playlist editedPlaylist = service.edit(playlist);
        if (editedPlaylist != null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(editedPlaylist)));
        else
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist could not be edited."));
    };

    public static final Route DELETE_PLAYLIST = (request, response) -> {
        service.delete(request.params(":id"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, "Playlist deleted"));
    };

    public static final Route OPTIONS_PLAYLIST = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
            (service.exists(request.params(":id")) ? "Playlist does exist" : "Playlist does not exist")));
}
