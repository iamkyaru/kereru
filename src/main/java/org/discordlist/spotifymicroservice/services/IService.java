package org.discordlist.spotifymicroservice.services;

import java.util.Collection;

public interface IService<T> {

    default void add(T t) {
        throw new UnsupportedOperationException("Not supported.");
    }

    Collection<T> getCachedValues();

    T get(String id);

    default T edit(T t) {
        throw new UnsupportedOperationException("Not supported.");
    }

    default void delete(String id) {
        throw new UnsupportedOperationException("Not supported.");
    }

    default boolean exists(String id) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
