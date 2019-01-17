package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.impl.TrackService;
import spark.Route;

public class TrackController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final TrackService service = SpotifyMicroservice.instance().trackService();

    public static final Route GET_CACHED_TRACKS = (request, response)
            -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Route GET_TRACK = (request, response) -> {
        String id = request.params(":id");
        if (!service.exists(id))
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, "Track does not exist"));
        return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCache().get(id))));
    };
}