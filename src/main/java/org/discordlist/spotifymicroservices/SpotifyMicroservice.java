package org.discordlist.spotifymicroservices;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.response.StandardResponse;
import org.discordlist.spotifymicroservices.services.Service;
import org.discordlist.spotifymicroservices.services.impl.TrackService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static spark.Spark.*;

public class SpotifyMicroservice {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private Service<Track> service = new TrackService();

    private SpotifyMicroservice() {
        port(1337);
        get("/", (request, response) -> "Hello World");

        path("/", () -> before("/*", (request, response) -> response.type("application/json")));
        post("/tracks", (request, response) -> {
            Track track = GSON.fromJson(request.body(), Track.class);
            this.service.add(track);
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS));
        });
        get("/tracks", (request, response)
                -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(this.service.getAll()))));
        get("/tracks/:id", (request, response)
                -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(this.service.get(request.params(":id"))))));
        put("/tracks/:id", (request, response) -> {
            Track track = GSON.fromJson(request.body(), Track.class);
            Track editedTrack = this.service.edit(track);
            if (editedTrack != null)
                return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJsonTree(editedTrack)));
            else
                return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.ERROR, GSON.toJson("Track not found or error in edit")));
        });
        delete("/tracks/:id", (request, response) -> {
            this.service.delete(request.params(":id"));
            return GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS, GSON.toJson("Track deleted")));
        });
        options("/tracks/:id", (request, response) -> GSON.toJson(new StandardResponse(StandardResponse.StatusResponse.SUCCESS,
                (this.service.exists(request.params(":id")) ? GSON.toJson("Track exists") : GSON.toJson("Track doesn't exist")))));
        postTrack();
    }

    private void postTrack() {
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Test Name");
        RequestBody requestBody = RequestBody.create(MediaType.get("application/json; charset=utf-8"), "{ \"name\": \"Test Name\", \"id\": \"1\"}");
        Request request = new Request.Builder().url("http://0.0.0.0:1337/tracks").post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            System.out.println(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SpotifyMicroservice();
    }
}
