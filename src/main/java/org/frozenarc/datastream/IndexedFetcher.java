package org.frozenarc.datastream;

/*
 * Author: mpanchal
 * Date: 15-02-2023
 */
public interface IndexedFetcher {

    byte[] fetch(int idx) throws DataStreamException;
}
