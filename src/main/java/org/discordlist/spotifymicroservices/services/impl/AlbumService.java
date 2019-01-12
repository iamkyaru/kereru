package org.discordlist.spotifymicroservices.services.impl;

import org.discordlist.spotifymicroservices.entities.Album;
import org.discordlist.spotifymicroservices.exceptions.AlbumException;
import org.discordlist.spotifymicroservices.services.IService;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlbumService implements IService<Album> {

    private final Map<String, Album> albumMap;

    public AlbumService() {
        this.albumMap = new ConcurrentHashMap<>();
    }

    /**
     * Add an Album to the in-memory cache.
     *
     * @param album the album which should be added.
     */
    @Override
    public void add(Album album) {
        if (album != null)
            this.albumMap.put(album.id(), album);
    }

    /**
     * Get all cached values.
     *
     * @return all cached values.
     */
    @Override
    public Collection<Album> getCachedValues() {
        return this.albumMap.values();
    }

    @Override
    public Album get(String id) {
        if (id != null)
            return this.albumMap.get(id);
        return null;
    }

    @Override
    public Album edit(Album album) throws AlbumException {
        try {
            if (album.id() == null || album.id().isEmpty())
                throw new AlbumException("Album id cannot be null");
            Album editedAlbum = this.albumMap.get(album.id());
            if (editedAlbum == null)
                throw new AlbumException("Album not found");
            if (album.artists() != null && !album.artists().isEmpty())
                editedAlbum.artists(album.artists());
            if (album.href() != null)
                editedAlbum.href(album.href());
            if (album.name() != null)
                editedAlbum.name(album.name());
            if (album.tracks() != null && !album.tracks().isEmpty())
                editedAlbum.tracks(album.tracks());
            if (album.uri() != null)
                editedAlbum.uri(album.uri());
            if (album.uri() != null)
                editedAlbum.uri(album.uri());
            return editedAlbum;
        } catch (Exception e) {
            throw new AlbumException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        if (id != null)
            this.albumMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.albumMap.containsKey(id);
        return false;
    }

    public Map<String, Album> getAlbumMap() {
        return this.albumMap;
    }
}
