package org.frozenarc.datastream;

import java.io.OutputStream;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:32
 * DataSteam
 * Interface to be implemented to handle specific kind of data stream.
 *
 * @param <T> represents node of the specific type
 */
public interface DataStream<T> {

    /**
     * use to check whether nodes are available in stream or not
     *
     * @return true if nodes are available else false
     * @throws DataStreamException if anything goes wrong
     */
    boolean hasNext() throws DataStreamException;

    /**
     * use to get next node of type T
     *
     * @return node of type T
     * @throws DataStreamException if anything goes wrong
     */
    T next() throws DataStreamException;
}
