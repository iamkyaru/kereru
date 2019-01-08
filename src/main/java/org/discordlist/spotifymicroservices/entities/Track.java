package org.discordlist.spotifymicroservices.entities;

import java.util.List;

public class Track {

    private List<String> artists;
    private long durationTimeMillis;
    private boolean explicit;
    private String url;
    private String href;
    private String id;
    private boolean playable;
    private String name;
    private String uri;

    public Track(List<String> artists,
                 long durationTimeMillis,
                 boolean explicit,
                 String url,
                 String href,
                 String id,
                 boolean playable,
                 String name,
                 String uri) {
        this.artists = artists;
        this.durationTimeMillis = durationTimeMillis;
        this.explicit = explicit;
        this.url = url;
        this.href = href;
        this.id = id;
        this.playable = playable;
        this.name = name;
        this.uri = uri;
    }

    public List<String> getArtists() {
        return artists;
    }

    public void setArtists(List<String> artists) {
        this.artists = artists;
    }

    public long getDurationTimeMillis() {
        return durationTimeMillis;
    }

    public void setDurationTimeMillis(long durationTimeMillis) {
        this.durationTimeMillis = durationTimeMillis;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
