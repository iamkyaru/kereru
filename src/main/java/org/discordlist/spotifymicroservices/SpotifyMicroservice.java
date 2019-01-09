package org.discordlist.spotifymicroservices;

import org.discordlist.spotifymicroservices.controller.PlaylistController;
import org.discordlist.spotifymicroservices.controller.TrackController;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.services.Service;
import org.discordlist.spotifymicroservices.services.impl.PlaylistService;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import static spark.Spark.*;

public class SpotifyMicroservice {

    private static Service<Track> trackService;
    private static Service<Playlist> playlistService;

    private SpotifyMicroservice() {
        trackService = new TrackService();
        playlistService = new PlaylistService();

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
        
        /* Playlists */
        post("/playlists", PlaylistController.POST_PLAYLIST);
        get("/playlists", PlaylistController.GET_PLAYLISTS);
        get("/playlists/:id", PlaylistController.GET_PLAYLIST);
        put("/playlists/:id", PlaylistController.PUT_PLAYLIST);
        delete("/playlists/:id", PlaylistController.DELETE_PLAYLIST);
        options("/playlists/:id", PlaylistController.OPTIONS_PLAYLIST);
    }

    public static Service<Track> getTrackService() {
        return trackService;
    }

    public static Service<Playlist> getPlaylistService() {
        return playlistService;
    }

    public static void main(String[] args) {
        new SpotifyMicroservice();
    }
}
