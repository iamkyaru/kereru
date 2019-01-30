package org.discordlist.spotifymicroservice.response;

import com.google.gson.JsonElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Getter
@Setter
public class StandardResponse {

    private final StatusResponse status;
    private String message;
    private JsonElement data;

    public StandardResponse(StatusResponse status) {
        this.status = status;
    }

    public StandardResponse(StatusResponse status, String message) {
        this.status = status;
        this.message = message;
    }

    public StandardResponse(StatusResponse status, JsonElement data) {
        this.status = status;
        this.data = data;
    }

    @Getter
    public enum StatusResponse {
        SUCCESS("Success"),
        ERROR("Error");

        private final String status;

        StatusResponse(String status) {
            this.status = status;
        }
    }
}