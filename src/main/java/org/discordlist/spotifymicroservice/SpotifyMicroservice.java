package org.discordlist.spotifymicroservice;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.discordlist.spotifymicroservice.cache.RedisSession;
import org.discordlist.spotifymicroservice.config.Config;
import org.discordlist.spotifymicroservice.controller.AlbumController;
import org.discordlist.spotifymicroservice.controller.ArtistController;
import org.discordlist.spotifymicroservice.controller.PlaylistController;
import org.discordlist.spotifymicroservice.controller.TrackController;
import org.discordlist.spotifymicroservice.requests.handler.TokenHandler;
import org.discordlist.spotifymicroservice.services.impl.AlbumService;
import org.discordlist.spotifymicroservice.services.impl.ArtistService;
import org.discordlist.spotifymicroservice.services.impl.PlaylistService;
import org.discordlist.spotifymicroservice.services.impl.TrackService;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;
import java.util.Objects;

import static spark.Spark.*;

@Log4j2
@Accessors(fluent = true)
@Getter
public class SpotifyMicroservice {

    @Getter
    private static SpotifyMicroservice instance;
    private final YamlFile config;
    private final TokenHandler tokenHandler;
    private final RedisSession redisSession;

    private final TrackService trackService;
    private final ArtistService artistService;
    private final PlaylistService playlistService;
    private final AlbumService albumService;

    private SpotifyMicroservice() throws IOException, InvalidConfigurationException {
        instance = this;
        this.config = new Config("config.yml").load();
        this.tokenHandler = new TokenHandler(config.getString(Config.SPOTIFY_CLIENT_ID), config.getString(Config.SPOTIFY_CLIENT_SECRET));
        this.redisSession = new RedisSession(config.getString(Config.REDIS_HOST), config.getInt(Config.REDIS_PORT), config.getString(Config.REDIS_PASSWORD));

        this.trackService = new TrackService(redisSession);
        this.artistService = new ArtistService();
        this.playlistService = new PlaylistService(redisSession);
        this.albumService = new AlbumService();

        port(config.getInt(Config.SERVICE_PORT));
        ipAddress(config.getString(Config.SERVICE_BIND));
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
            /* Playlists */
            get("/playlists", PlaylistController.GET_PLAYLISTS);
            get("/playlists/:id", PlaylistController.GET_PLAYLIST);
            get("/playlists/:id/tracks", PlaylistController.GET_PLAYLIST_TRACKS);
            get("/playlists/:id/tracks/:trackId", PlaylistController.GET_PLAYLIST_TRACK);
            /* Albums */
            get("/albums", AlbumController.GET_ALBUMS);
            get("/albums/:id", AlbumController.GET_ALBUM);
            get("/albums/:id/tracks", AlbumController.GET_ALBUM_TRACKS);
            get("/albums/:id/tracks/:trackId", AlbumController.GET_ALBUM_TRACK);
        });
    }

    public static void main(String[] args) throws IOException, InvalidConfigurationException {
        Configurator.setRootLevel(Level.toLevel(args[0], Level.INFO));
        Configurator.initialize(ClassLoader.getSystemClassLoader(), new ConfigurationSource(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("log4j2.xml"))));
        new SpotifyMicroservice();
    }
}
