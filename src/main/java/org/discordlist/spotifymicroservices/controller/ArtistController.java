package org.discordlist.spotifymicroservices.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.IService;
import spark.Route;

public class ArtistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final IService<Artist> service = SpotifyMicroservice.getInstance().getArtistService();

    public static final Route GET_ARTISTS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Route GET_ARTIST = (request, response) -> {
        String id = request.params(":id");
//        if (!service.exists(id))
//            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id))));
    };

    public static final Route GET_ARTISTS_TOP_TRACKS = (request, response) -> {
        String id = request.params(":id");
//        if (!service.exists(id))
//            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id).getTopTracks())));
    };

    public static final Route GET_ARTISTS_TOP_TRACK = (request, response) -> {
        String id = request.params(":id");
//        if (!service.exists(id))
//            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist does not exist"));
        String trackId = request.params(":trackId");
        Artist artist = service.get(id);
        Track track = artist.getTopTracks().stream().filter(t -> t.getId().equals(trackId)).findFirst().orElse(null);
        if (track == null || artist.getTopTracks().stream().noneMatch(t -> t.getId().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));
    };
}
