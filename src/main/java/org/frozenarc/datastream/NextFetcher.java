package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 * Interface to represent NextFetcher of any kind
 */
public interface NextFetcher {

    /**
     * Represents next of any custom kind
     *
     * @return byte[] data converting any kind of node
     * @throws DataStreamException if any problem occurs
     */
    byte[] next() throws DataStreamException;
}
