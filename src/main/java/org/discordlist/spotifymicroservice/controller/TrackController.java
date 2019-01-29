package org.discordlist.spotifymicroservice.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Handler;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;
import org.discordlist.spotifymicroservice.response.StandardResponse;
import org.discordlist.spotifymicroservice.services.impl.TrackService;

public class TrackController {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final TrackService service = SpotifyMicroservice.instance().trackService();

    public static final Handler GET_TRACKS = ctx -> ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCachedValues())));

    public static final Handler GET_TRACK = ctx -> {
        String trackId = ctx.pathParam(":trackId");
        ctx.json(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(service.getCache().get(trackId))));
    };
}