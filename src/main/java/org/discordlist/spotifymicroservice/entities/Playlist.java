package org.discordlist.spotifymicroservice.entities;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.discordlist.spotifymicroservice.cache.Cacheable;

import java.util.List;

@Builder
@Accessors(fluent = true)
@Getter
@Setter
public class Playlist implements Cacheable {

    private final String id;
    private String name;
    private String owner;
    private String url;
    private String href;
    private String uri;
    private List<Track> tracks;
}
