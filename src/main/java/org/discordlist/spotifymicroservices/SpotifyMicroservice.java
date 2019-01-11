package org.discordlist.spotifymicroservices;

import org.discordlist.spotifymicroservices.controller.ArtistController;
import org.discordlist.spotifymicroservices.controller.PlaylistController;
import org.discordlist.spotifymicroservices.controller.TrackController;
import org.discordlist.spotifymicroservices.entities.Album;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.services.IService;
import org.discordlist.spotifymicroservices.services.impl.ArtistService;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import static spark.Spark.*;

public class SpotifyMicroservice {

    private static IService<Track> trackService;
    private static IService<Artist> artistService;
    private static IService<Playlist> playlistService;
    private static IService<Album> albumService;

    private SpotifyMicroservice() {
        trackService = new TrackService("", "");
        artistService = new ArtistService("", "");
//        playlistService = new PlaylistService();
//        albumService = new AlbumService();

        port(1337);
        threadPool(16, 2, 30000);
        get("/", (request, response) -> {
            response.status(404);
            response.type("application/text");
            return "This endpoint is not available.";
        });
        path("/", () -> before("v1/*", (request, response) -> response.type("application/json")));
        path("/v1", () -> {
            /* Tracks */
            get("/tracks", TrackController.GET_CACHED_TRACKS);
            get("/tracks/:id", TrackController.GET_TRACK);
            /* Artists */
            get("/artists", ArtistController.GET_ARTISTS);
            get("/artists/:id", ArtistController.GET_ARTIST);
            get("/artists/:id/top-tracks", ArtistController.GET_ARTISTS_TOP_TRACKS);
            get("/artists/:id/top-tracks/:trackId", ArtistController.GET_ARTISTS_TOP_TRACK);
        });

//        /* Playlists */
//        get("/playlists", PlaylistController.GET_PLAYLISTS);
//        get("/playlists/:id", PlaylistController.GET_PLAYLIST);
//        get("/playlists/:id/tracks", PlaylistController.GET_PLAYLIST_TRACKS);
//        get("/playlists/:id/tracks/:trackId", PlaylistController.GET_PLAYLIST_TRACK);
//        /* Albums */
//        get("/albums", AlbumController.GET_ALBUMS);
//        get("/albums/:id", AlbumController.GET_ALBUM);
//        get("/albums/:id/tracks", AlbumController.GET_ALBUM_TRACKS);
//        get("/albums/:id/tracks/:trackId", AlbumController.GET_ALBUM_TRACK);
    }

    public static IService<Track> getTrackService() {
        return trackService;
    }

    public static IService<Playlist> getPlaylistService() {
        return playlistService;
    }

    public static IService<Album> getAlbumService() {
        return albumService;
    }

    public static IService<Artist> getArtistService() {
        return artistService;
    }

    public static void main(String[] args) {
        new SpotifyMicroservice();
    }
}
