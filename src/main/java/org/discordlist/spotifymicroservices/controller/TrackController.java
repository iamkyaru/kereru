package org.discordlist.spotifymicroservices.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.Service;
import spark.Route;

public class TrackController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Service<Track> service = SpotifyMicroservice.getTrackService();

    public static final Route POST_TRACK = (request, response) -> {
        if (request.body() == null || request.body().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Empty request body"));
        Track track = GSON.fromJson(request.body(), Track.class);
        if (track.getId() == null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track not found"));
        service.add(track);
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS));
    };

    public static final Route GET_TRACKS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCollection())));

    public static final Route GET_TRACK = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.get(id))));
    };

    public static final Route PUT_TRACK = (request, response) -> {
        Track track = GSON.fromJson(request.body(), Track.class);
        System.out.println(track.getId() + "-" + request.body());
        if (track.getId() == null || track.getId().isEmpty())
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track not found"));
        Track editedTrack = service.edit(track);
        if (editedTrack != null)
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(editedTrack)));
        else
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track could not be edited."));
    };

    public static final Route DELETE_TRACK = (request, response) -> {
        service.delete(request.params(":id"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, "Track deleted"));
    };

    public static final Route OPTIONS_TRACK = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
            (service.exists(request.params(":id")) ? "Track does exist" : "Track does not exist")));
}