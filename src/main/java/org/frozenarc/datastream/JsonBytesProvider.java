package org.frozenarc.datastream;

/*
 * Author: mpanchal
 * Date: 01-11-2022
 */
public interface JsonBytesProvider<D> {

    byte[] provide(D node) throws DataStreamException;
}
