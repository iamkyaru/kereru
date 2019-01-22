package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Handler;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.entities.Artist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.IService;

public class ArtistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final IService<Artist> service = SpotifyMicroservice.instance().artistService();

    public static final Handler GET_ARTISTS = context
            -> context.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Handler GET_ARTIST = context -> {
        String artistId = context.queryParam(":artistId");
        context.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(artistId))));
    };

    public static final Handler GET_ARTISTS_TOP_TRACKS = context -> {
        String artistId = context.queryParam(":artistId");
        context.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(artistId).topTracks())));
    };

    public static final Handler GET_ARTISTS_TOP_TRACK = context -> {
        String artistId = context.queryParam(":artistId");
        String trackId = context.queryParam(":trackId");
        Artist artist = service.get(artistId);
        Track track = artist.topTracks().stream().filter(t -> t.id().equals(trackId)).findFirst().orElse(null);
        if (track == null || artist.topTracks().stream().noneMatch(t -> t.id().equals(trackId)))
            context.json(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        context.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}
