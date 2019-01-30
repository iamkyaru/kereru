package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Handler;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.entities.Playlist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.impl.PlaylistService;

public class PlaylistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final PlaylistService service = SpotifyMicroservice.instance().playlistService();

    public static final Handler GET_PLAYLISTS = ctx
            -> ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Handler GET_PLAYLIST = ctx -> {
        String playlistId = ctx.pathParam(":playlistId");
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCache().get(playlistId))));
    };

    public static final Handler GET_PLAYLIST_TRACKS = ctx -> {
        String playlistId = ctx.pathParam(":playlistId");
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCache().get(playlistId).tracks())));
    };

    public static final Handler GET_PLAYLIST_TRACK = ctx -> {
        String playlistId = ctx.pathParam(":playlistId");
        String trackId = ctx.pathParam(":trackId");
        Playlist playlist = service.getCache().get(playlistId);
        Track track = playlist.tracks().stream().filter(t -> t.id().equals(trackId)).findFirst().orElse(null);
        if (track == null || playlist.tracks().stream().noneMatch(t -> t.id().equals(trackId)))
            ctx.json(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}
