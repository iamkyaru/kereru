package org.discordlist.spotifymicroservices;

import com.google.gson.Gson;
import org.discordlist.spotifymicroservices.controller.AlbumController;
import org.discordlist.spotifymicroservices.controller.ArtistController;
import org.discordlist.spotifymicroservices.controller.PlaylistController;
import org.discordlist.spotifymicroservices.controller.TrackController;
import org.discordlist.spotifymicroservices.entities.Album;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.services.Service;
import org.discordlist.spotifymicroservices.services.impl.AlbumService;
import org.discordlist.spotifymicroservices.services.impl.ArtistService;
import org.discordlist.spotifymicroservices.services.impl.PlaylistService;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import static spark.Spark.*;

public class SpotifyMicroservice {

    private static Service<Track> trackService;
    private static Service<Playlist> playlistService;
    private static Service<Album> albumService;
    private static Service<Artist> artistService;

    private SpotifyMicroservice() {
        trackService = new TrackService();
        playlistService = new PlaylistService();
        albumService = new AlbumService();
        artistService = new ArtistService();

        port(1337);
        threadPool(16, 2, 30000);
        get("/", (request, response) -> {
            response.status(404);
            response.type("application/text");
            return "This endpoint is not available.";
        });
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
        get("/playlists/:id/tracks", PlaylistController.GET_PLAYLIST_TRACKS);
        get("/playlists/:id/tracks/:trackId", PlaylistController.GET_PLAYLIST_TRACK);
        put("/playlists/:id", PlaylistController.PUT_PLAYLIST);
        delete("/playlists/:id", PlaylistController.DELETE_PLAYLIST);
        options("/playlists/:id", PlaylistController.OPTIONS_PLAYLIST);
        /* Albums */
        post("/albums", AlbumController.POST_ALBUM);
        get("/albums", AlbumController.GET_ALBUMS);
        get("/albums/:id", AlbumController.GET_ALBUM);
        get("/albums/:id/tracks", AlbumController.GET_ALBUM_TRACKS);
        get("/albums/:id/tracks/:trackId", AlbumController.GET_ALBUM_TRACK);
        put("/albums/:id", AlbumController.PUT_ALBUM);
        delete("/albums/:id", AlbumController.DELETE_ALBUM);
        options("/albums/:id", AlbumController.OPTIONS_ALBUM);
        /* Artists */
        post("/artists", ArtistController.POST_ARTIST);
        get("/artists", ArtistController.GET_ARTISTS);
        get("/artists/:id", ArtistController.GET_ARTIST);
        get("/artists/:id/top-tracks", ArtistController.GET_ARTISTS_TOP_TRACKS);
        get("/artists/:id/top-tracks/:trackId", ArtistController.GET_ARTISTS_TOP_TRACK);
        put("/artists/:id", ArtistController.PUT_ARTIST);
        delete("/artists/:id", ArtistController.DELETE_ARTIST);
        options("/artists/:id", ArtistController.OPTIONS_ARTIST);
    }

    public static Service<Track> getTrackService() {
        return trackService;
    }

    public static Service<Playlist> getPlaylistService() {
        return playlistService;
    }

    public static Service<Album> getAlbumService() {
        return albumService;
    }

    public static Service<Artist> getArtistService() {
        return artistService;
    }

    public static void main(String[] args) {
        new SpotifyMicroservice();
    }
}
