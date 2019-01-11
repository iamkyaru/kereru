package org.discordlist.spotifymicroservices.entities;

import org.discordlist.spotifymicroservices.cache.Cacheable;

import java.util.List;

public class Track implements Cacheable {

    private final String id;
    private String name;
    private List<Artist> artists;
    private String url;
    private long durationTimeMillis;
    private String href;
    private String uri;
    private boolean local;
    private boolean explicit;

    public Track(String id, String name, List<Artist> artists, String url, long durationTimeMillis, String href, String uri, boolean local, boolean explicit) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.url = url;
        this.durationTimeMillis = durationTimeMillis;
        this.href = href;
        this.uri = uri;
        this.local = local;
        this.explicit = explicit;
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

    public List<Artist> getArtists() {
        return this.artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDurationTimeMillis() {
        return this.durationTimeMillis;
    }

    public void setDurationTimeMillis(long durationTimeMillis) {
        this.durationTimeMillis = durationTimeMillis;
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

    public boolean isLocal() {
        return this.local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }
}
