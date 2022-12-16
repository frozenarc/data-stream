package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 * Used tp convert node of type D into byte[]
 *
 * @param <D>
 */
public interface JsonBytesConvertor<D> {

    byte[] convert(D node) throws DataStreamException;
}
