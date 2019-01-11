package org.discordlist.spotifymicroservices.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRequest {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractRequest.class);
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final String API_BASE = "https://api.spotify.com/v1";
    protected OkHttpClient httpClient;

    private final String clientId, clientSecret;
    private String accessToken;
    private long tokenExpireTime;

    protected AbstractRequest(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        refreshTokenIfNecessary();
        this.httpClient = this.httpClient.newBuilder()
                .addInterceptor(chain -> {
                    Request.Builder builder = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + accessToken);
                    return chain.proceed(builder.build());
                })
                .build();
    }

    private void refreshTokenIfNecessary() {
        if (System.currentTimeMillis() > this.tokenExpireTime) try {
            retrieveAccessToken();
        } catch (Exception e) {
            logger.error("Could not refresh the access token!", e);
        }
    }

    private void retrieveAccessToken() {
        if (this.clientId.isEmpty() || this.clientSecret.isEmpty()) {
            logger.info("The clientId or the clientSecret haven't been set correctly! Please configure your Spotify credentials, to use the Spotify api.");
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
                    this.tokenExpireTime = System.currentTimeMillis() + (jsonObject.get("expires_in").getAsLong() * 1000);
                    logger.info("Received access token: {} which expires in: {} seconds.", accessToken, tokenExpireTime);
                }
            }
        } catch (IOException e) {
            logger.error("The access token could not be retrieved.", e);
        }
    }
}