package org.discordlist.spotifymicroservices.services.impl;

import org.discordlist.spotifymicroservices.entities.Track;
import org.discordlist.spotifymicroservices.exceptions.TrackException;
import org.discordlist.spotifymicroservices.services.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrackService implements Service<Track> {

    private Map<String, Track> trackMap;

    public TrackService() {
        this.trackMap = new HashMap<>();
    }

    @Override
    public void add(Track track) {
        if (track != null)
            this.trackMap.put(track.getId(), track);
    }

    @Override
    public Collection<Track> getCollection() {
        return trackMap.values();
    }

    @Override
    public Track get(String id) {
        if (id != null)
            return this.trackMap.get(id);
        return null;
    }

    @Override
    public Track edit(Track track) throws TrackException {
        try {
            if (track.getId() == null || track.getId().isEmpty())
                throw new TrackException("Track id cannot be null");
            Track editedTrack = this.trackMap.get(track.getId());
            if (editedTrack == null)
                throw new TrackException("Track not found");
            if (track.getArtists() != null)
                editedTrack.setArtists(track.getArtists());
            if (track.getDurationTimeMillis() != 0)
                editedTrack.setDurationTimeMillis(track.getDurationTimeMillis());
            if (track.getUrl() != null)
                editedTrack.setUrl(track.getUrl());
            if (track.isExplicit())
                editedTrack.setExplicit(track.isExplicit());
            if (track.getHref() != null)
                editedTrack.setHref(track.getHref());
            if (track.getName() != null)
                editedTrack.setName(track.getName());
            if (track.isPlayable())
                editedTrack.setPlayable(track.isPlayable());
            if (track.getUri() != null)
                editedTrack.setUri(track.getUri());

            return editedTrack;
        } catch (Exception e) {
            throw new TrackException(e.getMessage());
        }
    }

    @Override
    public void delete(String id) {
        if (id != null)
            this.trackMap.remove(id);
    }

    @Override
    public boolean exists(String id) {
        if (id != null)
            return this.trackMap.containsKey(id);
        return false;
    }
}
