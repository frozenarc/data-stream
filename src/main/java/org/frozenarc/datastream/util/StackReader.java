package org.frozenarc.datastream.util;

import org.frozenarc.datastream.DataStreamException;

/**
 * Author: mpanchal
 * Date: 2023-12-15 10:19
 */
public class StackReader<T> {

    private final StreamFetcher<T, T> fetcher;

    public StackReader(StreamFetcher<T, T> fetcher) {
        this.fetcher = fetcher;
    }

    public T pop() throws DataStreamException {
        if (fetcher.hasNext()) {
            return fetcher.next();
        } else {
            return null;
        }
    }

    public T peek() {
        return fetcher.getLast();
    }
}
