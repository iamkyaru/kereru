package org.discordlist.spotifymicroservice.requests.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Log4j2
public class TokenHandler {

    private OkHttpClient httpClient;
    private String clientId, clientSecret;

    private String accessToken;
    private long tokenExpireTime;

    public TokenHandler(String clientId, String clientSecret) {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        retrieveAccessToken();
    }

    public String token() {
        refreshTokenIfNecessary();
        return this.accessToken;
    }

    private void refreshTokenIfNecessary() {
        if (System.currentTimeMillis() > this.tokenExpireTime) try {
            retrieveAccessToken();
        } catch (Exception e) {
            log.error("[TokenHandler] Could not refresh the access token!", e);
        }
    }

    private void retrieveAccessToken() {
        if (this.clientId.isEmpty() || this.clientSecret.isEmpty()) {
            log.info("[TokenHandler] The clientId or the clientSecret haven't been set correctly! Please configure your Spotify credentials, to use the Spotify api.");
            return;
        }

        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("grant_type", "client_credentials");
        Request.Builder builder = new Request.Builder()
                .post(formBody.build())
                .header("Authorization", Credentials.basic(this.clientId, this.clientSecret))
                .url("https://accounts.spotify.com/api/token");
        try (Response response = httpClient.newCall(builder.build()).execute()) {
            if (response.body() != null) {
                JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();
                if (jsonObject.has("access_token")) {
                    this.accessToken = jsonObject.get("access_token").getAsString();
                    this.tokenExpireTime = System.currentTimeMillis() + (jsonObject.get("expires_in").getAsInt() * 1000);
                    log.info("[TokenHandler] Received access token: * which expires in: {} seconds.", jsonObject.get("expires_in").getAsInt());
                }
            }
        } catch (IOException e) {
            log.error("[TokenHandler] The access token could not be retrieved.", e);
        }
    }
}
