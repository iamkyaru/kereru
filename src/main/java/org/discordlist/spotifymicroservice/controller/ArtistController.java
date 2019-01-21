package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.entities.Artist;
import org.discordlist.spotifymicroservice.entities.Track;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.IService;
import spark.Route;

public class ArtistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final IService<Artist> service = SpotifyMicroservice.instance().artistService();

    public static final Route GET_ARTISTS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Route GET_ARTIST = (request, response) -> {
        String id = request.params(":id");
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id))));
    };

    public static final Route GET_ARTISTS_TOP_TRACKS = (request, response) -> {
        String id = request.params(":id");
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id).topTracks())));
    };

    public static final Route GET_ARTISTS_TOP_TRACK = (request, response) -> {
        String id = request.params(":id");
        String trackId = request.params(":trackId");
        Artist artist = service.get(id);
        Track track = artist.topTracks().stream().filter(t -> t.id().equals(trackId)).findFirst().orElse(null);
        if (track == null || artist.topTracks().stream().noneMatch(t -> t.id().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}
