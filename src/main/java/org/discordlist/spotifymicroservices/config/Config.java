package org.discordlist.spotifymicroservices.config;

import lombok.extern.log4j.Log4j2;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;

@Log4j2
public class Config {

    public static String SERVICE_PORT = "service.port";
    public static String SERVICE_BIND = "service.bind";
    public static String REDIS_HOST = "redis.host";
    public static String REDIS_PORT = "redis.port";
    public static String REDIS_PASSWORD = "redis.password";
    public static String SPOTIFY_CLIENT_ID = "spotify.client.id";
    public static String SPOTIFY_CLIENT_SECRET = "spotify.client.secret";

    private final String configSrc;
    private YamlFile config;

    public Config(String file) {
        this.configSrc = file;
    }

    public YamlFile load() throws IOException, InvalidConfigurationException {
        config = new YamlFile(configSrc);
        config.createNewFile(false);
        config.load();
        setDefaults();
        config.save();
        log.info("[Config] Loaded.");
        return config;
    }

    private void setDefaults() {
        // Service
        saveDefault(SERVICE_PORT, 1337);
        saveDefault(SERVICE_BIND, "localhost");

        // Redis
        saveDefault(REDIS_HOST, "localhost");
        saveDefault(REDIS_PORT, 6379);
        saveDefault(REDIS_PASSWORD, "");

        // Spotify
        saveDefault(SPOTIFY_CLIENT_ID, "1337");
        saveDefault(SPOTIFY_CLIENT_SECRET, "#itsmemario");
    }

    private void saveDefault(String path, Object value) {
        if (!config.isSet(path))
            config.set(path, value);
    }
}
