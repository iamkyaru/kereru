package org.discordlist.spotifymicroservices.exceptions;

public class PlaylistException extends Exception {

    public PlaylistException() {
        super();
    }

    public PlaylistException(String message) {
        super(message);
    }
}
