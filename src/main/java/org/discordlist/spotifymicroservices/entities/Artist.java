package org.discordlist.spotifymicroservices.entities;

import org.discordlist.spotifymicroservices.cache.Cacheable;

import java.util.List;

public class Artist implements Cacheable {

    private final String id;
    private String name;
    private String url;
    private String href;
    private String uri;
    private List<Track> topTracks;

    public Artist(String id, String name, String url, String href, String uri, List<Track> topTracks) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.href = href;
        this.uri = uri;
        this.topTracks = topTracks;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Track> getTopTracks() {
        return this.topTracks;
    }

    public void setTopTracks(List<Track> topTracks) {
        this.topTracks = topTracks;
    }
}