package org.discordlist.spotifymicroservices.entities;

import java.util.List;

public class Playlist {

    private String id;
    private String name;
    private String owner;
    private String url;
    private String href;
    private String uri;
    private List<Track> tracks;

    public Playlist(String id, String name, String owner, String url, String href, String uri, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.url = url;
        this.href = href;
        this.uri = uri;
        this.tracks = tracks;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<Track> getTracks() {
        return this.tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
