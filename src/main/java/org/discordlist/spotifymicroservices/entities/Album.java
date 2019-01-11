package org.discordlist.spotifymicroservices.entities;

import org.discordlist.spotifymicroservices.cache.Cacheable;

import java.util.List;

public class Album implements Cacheable {

    private final String id;
    private String name;
    private List<String> artists;
    private String url;
    private String href;
    private String uri;
    private List<Track> tracks;

    public Album(String id, String name, List<String> artists, String url, String href, String uri, List<Track> tracks) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.url = url;
        this.href = href;
        this.uri = uri;
        this.tracks = tracks;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtists() {
        return this.artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
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
