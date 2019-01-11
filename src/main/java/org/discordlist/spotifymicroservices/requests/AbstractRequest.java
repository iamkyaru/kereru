package org.discordlist.spotifymicroservices.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.discordlist.spotifymicroservices.SpotifyMicroservice;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public abstract class AbstractRequest {

    protected static final Logger logger = Logger.getLogger("AbstractRequest");
    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final String API_BASE = "https://api.spotify.com/v1";
    protected OkHttpClient httpClient;

    protected AbstractRequest() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request.Builder builder = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + SpotifyMicroservice.getInstance().getTokenHandler().getToken());
                    return chain.proceed(builder.build());
                })
                .build();
    }
}