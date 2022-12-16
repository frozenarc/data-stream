package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 2022-12-16 13:49
 * Creates and supplies DataStream
 *
 * @param <T>
 */
public interface StreamSupplier<T> {

    /**
     * Creates and supplies DataStream
     *
     * @return DataStream
     * @throws DataStreamException if anything goes wrong
     */
    DataStream<T> get() throws DataStreamException;
}
