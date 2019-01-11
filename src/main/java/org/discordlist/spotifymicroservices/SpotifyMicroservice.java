package org.discordlist.spotifymicroservices;

import org.discordlist.spotifymicroservices.configuration.Configuration;
import org.discordlist.spotifymicroservices.configuration.ConfigurationSetup;
import org.discordlist.spotifymicroservices.controller.ArtistController;
import org.discordlist.spotifymicroservices.controller.TrackController;
import org.discordlist.spotifymicroservices.entities.Album;
import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.requests.handler.TokenHandler;
import org.discordlist.spotifymicroservices.services.IService;
import org.discordlist.spotifymicroservices.services.impl.ArtistService;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import static spark.Spark.*;

public class SpotifyMicroservice {

    private static SpotifyMicroservice instance;
    private final Configuration config;
    private final TokenHandler tokenHandler;

    private IService<Track> trackService;
    private IService<Artist> artistService;
    private IService<Playlist> playlistService;
    private IService<Album> albumService;

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

    public IService<Track> getTrackService() {
        return this.trackService;
    }

    public IService<Playlist> getPlaylistService() {
        return this.playlistService;
    }

    public IService<Album> getAlbumService() {
        return this.albumService;
    }

    public IService<Artist> getArtistService() {
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
