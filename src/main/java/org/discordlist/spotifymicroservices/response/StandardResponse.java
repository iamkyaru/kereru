package org.discordlist.spotifymicroservices.response;

import com.google.gson.JsonElement;

@SuppressWarnings("unused")
public class StandardResponse {

    private StatusResponse status;
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

    public StatusResponse getStatus() {
        return this.status;
    }

    public StandardResponse setStatus(StatusResponse status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public StandardResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public JsonElement getData() {
        return this.data;
    }

    public StandardResponse setData(JsonElement data) {
        this.data = data;
        return this;
    }

    public enum StatusResponse {
        SUCCESS("Success"),
        ERROR("Error");

        private final String status;

        StatusResponse(String status) {
            this.status = status;
        }

        public String getStatus() {
            return this.status;
        }
    }
}