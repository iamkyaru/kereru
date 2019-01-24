package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Handler;
import lombok.extern.log4j.Log4j2;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.entities.Album;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.IService;

@Log4j2
public class AlbumController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final IService<Album> service = SpotifyMicroservice.instance().albumService();

    public static final Handler GET_ALBUMS = ctx
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Handler GET_ALBUM = ctx -> {
        String albumId = ctx.queryParam(":albumId");
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(albumId))));
    };

    public static final Handler GET_ALBUM_TRACKS = ctx -> {
        String albumId = ctx.queryParam(":albumId");
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(albumId).tracks())));
    };

    public static final Handler GET_ALBUM_TRACK = ctx -> {
        String albumId = ctx.queryParam(":albumId");
        String trackId = ctx.queryParam(":trackId");
        Album album = service.get(albumId);
        Track track = album.tracks().stream().filter(t -> t.id().equals(trackId)).findFirst().orElse(null);
        if (track == null || album.tracks().stream().noneMatch(t -> t.id().equals(trackId)))
            ctx.json(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}