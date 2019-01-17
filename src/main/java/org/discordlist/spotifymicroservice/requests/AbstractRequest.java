package org.discordlist.spotifymicroservice.requests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.discordlist.spotifymicroservice.SpotifyMicroservice;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class AbstractRequest {

    protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected static final String API_BASE = "https://api.spotify.com/v1";
    protected final OkHttpClient httpClient;

    protected AbstractRequest() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request.Builder builder = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer " + SpotifyMicroservice.instance().tokenHandler().token());
                    return chain.proceed(builder.build());
                })
                .build();
    }

    protected String getParamValue(String url, String parameter) throws URISyntaxException {
        List<NameValuePair> queryParams = new URIBuilder(url).getQueryParams();
        return queryParams.stream()
                .filter(param -> param.getName().equalsIgnoreCase(parameter))
                .map(NameValuePair::getValue)
                .findFirst()
                .orElse("");
    }
}