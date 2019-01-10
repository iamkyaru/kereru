package org.discordlist.spotifymicroservices.services.impl;

import org.discordlist.spotifymicroservices.entities.Artist;
import org.discordlist.spotifymicroservices.exceptions.ArtistException;
import org.discordlist.spotifymicroservices.services.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArtistService implements Service<Artist> {

    private final Map<String, Artist> artistMap;

    public ArtistService() {
        this.artistMap = new HashMap<>();
    }

    @Override
    public void add(Artist artist) {
        if (artist != null)
            this.artistMap.put(artist.getId(), artist);
    }

    @Override
    public Collection<Artist> getCachedValues() {
        return this.artistMap.values();
    }

    @Override
    public Artist get(String id) {
        if (id != null)
            return this.artistMap.get(id);
        return null;
    }

    @Override
    public Artist edit(Artist artist) throws ArtistException {
        try {
            if (artist.getId() == null || artist.getId().isEmpty())
                throw new ArtistException("Artist id cannot be null");
            Artist editedPlaylist = this.artistMap.get(artist.getId());
            if (editedPlaylist == null)
                throw new ArtistException("Artist not found");
            if (artist.getHref() != null)
                editedPlaylist.setHref(artist.getHref());
            if (artist.getName() != null)
                editedPlaylist.setName(artist.getName());
            if (artist.getTopTracks() != null && !artist.getTopTracks().isEmpty())
                editedPlaylist.setTopTracks(artist.getTopTracks());
            if (artist.getUri() != null)
                editedPlaylist.setUri(artist.getUri());
            if (artist.getUrl() != null)
                editedPlaylist.setUrl(artist.getUrl());
            return editedPlaylist;
        } catch (Exception e) {
            throw new ArtistException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        if (id != null)
            this.artistMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.artistMap.containsKey(id);
        return false;
    }
}
