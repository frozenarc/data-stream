package org.frozenarc.datastream.ext;

import org.frozenarc.datastream.DataStreamException;

/**
 * Provides way to work on stream as stake way
 * Please, not that the nature of stream is not same as stack, so the methods `pop` and `peek` will not work as same as stack
 * Author: mpanchal
 * Date: 2023-12-15 10:19
 */
public class StackReader<T> {

    private final StreamFetcher<T, T> fetcher;

    public StackReader(StreamFetcher<T, T> fetcher) {
        this.fetcher = fetcher;
    }

    /**
     * The method pops a node from stream, this will not automatically move to next node until next doesn't happen
     * @return returns T type of node
     * @throws DataStreamException thrown if anything gets wrong
     */
    public T pop() throws DataStreamException {
        if (fetcher.hasNext()) {
            return fetcher.next();
        } else {
            return null;
        }
    }

    /**
     * The method peeks node which is popped last, if nothing popped last it will try to pop and return
     * @return returns T type of node
     * @throws DataStreamException thrown if anything gets wrong
     */
    public T peek() throws DataStreamException {
        if (fetcher.getLast() == null) {
            return pop();
        }
        return fetcher.getLast();
    }
}

