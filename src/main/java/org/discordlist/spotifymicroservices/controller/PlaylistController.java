package org.discordlist.spotifymicroservices.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.impl.PlaylistService;
import spark.Route;

public class PlaylistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final PlaylistService service = SpotifyMicroservice.getInstance().getPlaylistService();

    public static final Route GET_PLAYLISTS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Route GET_PLAYLIST = (request, response) -> {
        String id = request.params(":id");
//        if (!service.exists(id))
//            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCache().get(id))));
    };

    public static final Route GET_PLAYLIST_TRACKS = (request, response) -> {
        String id = request.params(":id");
//        if (!service.exists(id))
//            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCache().get(id).getTracks())));
    };

    public static final Route GET_PLAYLIST_TRACK = (request, response) -> {
        String id = request.params(":id");
//        if (!service.exists(id)) {
//            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Playlist does not exist"));
//        }
        String trackId = request.params(":trackId");
        Playlist playlist = service.getCache().get(id);
        Track track = playlist.getTracks().stream().filter(t -> t.getId().equals(trackId)).findFirst().orElse(null);
        if (track == null || playlist.getTracks().stream().noneMatch(t -> t.getId().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}
