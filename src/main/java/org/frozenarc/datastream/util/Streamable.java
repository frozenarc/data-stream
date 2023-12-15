package org.frozenarc.datastream.util;

/**
 * Implement the interface to delegate stream fetching functionality to StreamFetcher
 * Author: mpanchal
 * Date: 2023-12-15 10:35
 */
public interface Streamable<T> {

    /**
     * Underlying implemented class should return StreamFetcher
     * @return StreamFetcher
     */
    StreamFetcher<T, T> streamFetcher();

}
