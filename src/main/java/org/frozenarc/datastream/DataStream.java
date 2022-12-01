package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:32
 */
public interface DataStream<T> {

    boolean hasNext() throws DataStreamException;

    T next() throws DataStreamException;
}
