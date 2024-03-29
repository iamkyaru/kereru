package org.discordlist.spotifymicroservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import io.javalin.json.JavalinJson;
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

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

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
        this.artistService = new ArtistService(redisSession);
        this.playlistService = new PlaylistService(redisSession);
        this.albumService = new AlbumService(redisSession);

        Javalin app = Javalin.create()
                .port(config.getInt(Config.SERVICE_PORT))
                .defaultContentType("application/json")
                .enableCaseSensitiveUrls()
                .requestLogger((ctx, executionTimeMs) -> log.info(ctx.method() + " " + ctx.path() + " took " + executionTimeMs + "ms"))
                .start();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JavalinJson.setFromJsonMapper(gson::fromJson);
        JavalinJson.setToJsonMapper(gson::toJson);
        app.routes(() -> {
            get("/", ctx -> ctx.status(405).result("Not available."));
            path("v1", () -> {
                path("tracks", () -> {
                    get("/", TrackController.GET_TRACKS);
                    get("/:trackId", TrackController.GET_TRACK);
                });
                path("artists", () -> {
                    get("/", ArtistController.GET_ARTISTS);
                    path("/:artistId", () -> {
                        get("/", ArtistController.GET_ARTIST);
                        get("/top-tracks", ArtistController.GET_ARTISTS_TOP_TRACKS);
                        get("/top-tracks/:trackId", ArtistController.GET_ARTISTS_TOP_TRACKS);
                    });
                });
                path("playlists", () -> {
                    get("/", PlaylistController.GET_PLAYLISTS);
                    path("/:playlistId", () -> {
                        get("/", PlaylistController.GET_PLAYLIST);
                        get("/tracks", PlaylistController.GET_PLAYLIST_TRACKS);
                        get("/tracks/:trackId", PlaylistController.GET_PLAYLIST_TRACK);
                    });
                });
                path("albums", () -> {
                    get("/", AlbumController.GET_ALBUMS);
                    path("/:albumId", () -> {
                        get("/", AlbumController.GET_ALBUM);
                        get("/tracks", AlbumController.GET_ALBUM_TRACKS);
                        get("/tracks/:trackId", AlbumController.GET_ALBUM_TRACK);
                    });
                });
            });
        });

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    public static void main(String[] args) throws IOException, InvalidConfigurationException {
        Configurator.setRootLevel(Level.toLevel(args.length == 0 ? "" : args[0], Level.INFO));
        Configurator.initialize(ClassLoader.getSystemClassLoader(), new ConfigurationSource(Objects.requireNonNull(ClassLoader.getSystemResourceAsStream("log4j2.xml"))));
        new SpotifyMicroservice();
    }
}
