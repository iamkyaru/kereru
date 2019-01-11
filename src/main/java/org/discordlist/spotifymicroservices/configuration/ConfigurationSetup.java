package org.discordlist.spotifymicroservices.configuration;

import org.json.JSONObject;

public class ConfigurationSetup {

    public static Configuration setupConfig() {
        Configuration configuration = new Configuration("config/config.json");

        final JSONObject spotify = new JSONObject()
                .put("client_id", "")
                .put("client_secret", "");
        configuration.addDefault("spotify", spotify);

        return configuration;
    }
}
