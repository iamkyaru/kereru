package org.discordlist.spotifymicroservices.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.Service;
import spark.Route;

public class ArtistController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Service<Artist> service = SpotifyMicroservice.getArtistService();

    public static final Route POST_ARTIST = (request, response) -> {
        if (request.body() == null || request.body().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Empty request body"));
        Artist artist = GSON.fromJson(request.body(), Artist.class);
        if (artist.getId() == null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist not found"));
        service.add(artist);
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS));};

    public static final Route GET_ARTISTS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Route GET_ARTIST = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id))));
    };

    public static final Route GET_ARTISTS_TOP_TRACKS = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id).getTopTracks())));};

    public static final Route GET_ARTISTS_TOP_TRACK = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id)) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist does not exist"));
        }
        String trackId = request.params(":trackId");
        Artist artist = service.get(id);
        Track track = artist.getTopTracks().stream().filter(t -> t.getId().equals(trackId)).findFirst().orElse(null);
        if (track == null || artist.getTopTracks().stream().noneMatch(t -> t.getId().equals(trackId))) {
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        }
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(track)));};

    public static final Route PUT_ARTIST = (request, response) -> {
        Artist artist = GSON.fromJson(request.body(), Artist.class);
        if (artist.getId() == null || artist.getId().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist not found"));
        Artist editedArtist = service.edit(artist);
        if (editedArtist != null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(editedArtist)));
        else
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Artist could not be edited."));};

    public static final Route DELETE_ARTIST = (request, response) -> {
        service.delete(request.params(":id"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, "Artist deleted"));};

    public static final Route OPTIONS_ARTIST = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
            (service.exists(request.params(":id")) ? "Artist does exist" : "Artist does not exist")));
}
