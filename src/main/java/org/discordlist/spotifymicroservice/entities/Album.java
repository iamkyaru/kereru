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
public class Album implements Cacheable {

    private final String id;
    private String name;
    private List<Artist> artists;
    private String url;
    private String href;
    private String uri;
    private List<Track> tracks;
}
