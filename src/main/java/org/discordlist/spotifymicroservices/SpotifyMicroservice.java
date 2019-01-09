package org.discordlist.spotifymicroservices;

import org.discordlist.spotifymicroservices.controller.TrackController;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.services.Service;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import static spark.Spark.*;

public class SpotifyMicroservice {

    private static Service<Track> service;

    private SpotifyMicroservice() {
        service = new TrackService();

        port(1337);
        get("/", (request, response) -> "Hello World");
        path("/", () -> before("/*", (request, response) -> response.type("application/json")));
        /* Tracks */
        post("/tracks", TrackController.POST_TRACK);
        get("/tracks", TrackController.GET_TRACKS);
        get("/tracks/:id", TrackController.GET_TRACK);
        put("/tracks/:id", TrackController.PUT_TRACK);
        delete("/tracks/:id", TrackController.DELETE_TRACK);
        options("/tracks/:id", TrackController.OPTIONS_TRACK);
    }

    public static Service<Track> getTrackService() {
        return service;
    }

    public static void main(String[] args) {
        new SpotifyMicroservice();
    }
}
