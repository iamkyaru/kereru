package org.discordlist.spotifymicroservices;

import org.discordlist.spotifymicroservices.configuration.Configuration;
import org.discordlist.spotifymicroservices.configuration.ConfigurationSetup;
import org.discordlist.spotifymicroservices.controller.ArtistController;
import org.discordlist.spotifymicroservices.controller.TrackController;
import org.discordlist.spotifymicroservices.requests.handler.TokenHandler;
import org.discordlist.spotifymicroservices.services.impl.AlbumService;
import org.discordlist.spotifymicroservices.services.impl.ArtistService;
import org.discordlist.spotifymicroservices.services.impl.PlaylistService;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import static spark.Spark.*;

public class SpotifyMicroservice {

    private static SpotifyMicroservice instance;
    private final Configuration config;
    private final TokenHandler tokenHandler;

    private TrackService trackService;
    private ArtistService artistService;
    private PlaylistService playlistService;
    private AlbumService albumService;

    private SpotifyMicroservice() {
        instance = this;
        config = ConfigurationSetup.setupConfig().init();
        tokenHandler = new TokenHandler(config.getJSONObject("spotify"));

        trackService = new TrackService();
        artistService = new ArtistService();
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

    public Configuration getConfig() {
        return this.config;
    }

    public TrackService getTrackService() {
        return this.trackService;
    }

    public PlaylistService getPlaylistService() {
        return this.playlistService;
    }

    public AlbumService getAlbumService() {
        return this.albumService;
    }

    public ArtistService getArtistService() {
        return this.artistService;
    }

    public TokenHandler getTokenHandler() {
        return this.tokenHandler;
    }

    public static SpotifyMicroservice getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new SpotifyMicroservice();
    }
}
