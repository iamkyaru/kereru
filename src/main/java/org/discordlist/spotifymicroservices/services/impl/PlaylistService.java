package org.discordlist.spotifymicroservices.services.impl;

import org.discordlist.spotifymicroservices.entities.Playlist;
import org.discordlist.spotifymicroservices.exceptions.PlaylistException;
import org.discordlist.spotifymicroservices.services.IService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlaylistService implements IService<Playlist> {

    private final Map<String, Playlist> playlistMap;

    public PlaylistService() {
        this.playlistMap = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Playlist playlist) {
        if (playlist != null)
            this.playlistMap.put(playlist.getId(), playlist);
    }

    @Override
    public Collection<Playlist> getCachedValues() {
        return this.playlistMap.values();
    }

    @Override
    public Playlist get(String id) {
        if (id != null && this.playlistMap.containsKey(id))
            return this.playlistMap.get(id);
        return null;
    }

    @Override
    public Playlist edit(Playlist playlist) throws PlaylistException {
        try {
            if (playlist.getId() == null || playlist.getId().isEmpty())
                throw new PlaylistException("Playlist id cannot be null");
            Playlist editedPlaylist = this.playlistMap.get(playlist.getId());
            if (editedPlaylist == null)
                throw new PlaylistException("Playlist not found");
            if (playlist.getHref() != null)
                editedPlaylist.setHref(playlist.getHref());
            if (playlist.getName() != null)
                editedPlaylist.setName(playlist.getName());
            if (playlist.getOwner() != null)
                editedPlaylist.setOwner(playlist.getOwner());
            if (playlist.getTracks() != null && !playlist.getTracks().isEmpty())
                editedPlaylist.setTracks(playlist.getTracks());
            if (playlist.getUri() != null)
                editedPlaylist.setUri(playlist.getUri());
            if (playlist.getUrl() != null)
                editedPlaylist.setUrl(playlist.getUrl());
            return editedPlaylist;
        } catch (Exception e) {
            throw new PlaylistException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        if (id != null)
            this.playlistMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.playlistMap.containsKey(id);
        return false;
    }

    @SuppressWarnings("unused")
    public Map<String, Playlist> getPlaylistMap() {
        return this.playlistMap;
    }
}
