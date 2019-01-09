package org.discordlist.spotifymicroservices.entities;

import java.util.List;

public class Track {

    private String id;
    private String name;
    private List<String> artists;
    private String url;
    private long durationTimeMillis;
    private String href;
    private String uri;
    private boolean playable;
    private boolean explicit;

    public Track(String id, String name, List<String> artists, String url, long durationTimeMillis, String href, String uri, boolean playable, boolean explicit) {
        this.id = id;
        this.name = name;
        this.artists = artists;
        this.url = url;
        this.durationTimeMillis = durationTimeMillis;
        this.href = href;
        this.uri = uri;
        this.playable = playable;
        this.explicit = explicit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDurationTimeMillis() {
        return durationTimeMillis;
    }

    public void setDurationTimeMillis(long durationTimeMillis) {
        this.durationTimeMillis = durationTimeMillis;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }
}
