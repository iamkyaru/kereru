package org.discordlist.spotifymicroservices.config;

import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

import java.io.IOException;

public class Config {

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
        return config;
    }

    private void setDefaults() {
        saveDefault(SPOTIFY_CLIENT_ID, "1337");
        saveDefault(SPOTIFY_CLIENT_SECRET, "#itsmemario");
    }

    private void saveDefault(String path, Object value) {
        if (!config.isSet(path))
            config.set(path, value);
    }
}
