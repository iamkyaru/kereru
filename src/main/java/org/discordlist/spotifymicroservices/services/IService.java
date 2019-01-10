package org.discordlist.spotifymicroservices.services;

import java.util.Collection;

public interface IService<T> {

    void add(T t);

    Collection<T> getCachedValues();

    T get(String id);

    T edit(T t) throws Exception;

    void delete(String id);

    boolean exists(String id);
}
