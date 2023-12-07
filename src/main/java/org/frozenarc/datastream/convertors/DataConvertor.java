package org.frozenarc.datastream.convertors;

import org.frozenarc.datastream.DataStreamException;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 * Used tp convert node of type D into byte[]
 *
 * @param <D>
 */
public interface DataConvertor<D> {

    byte[] convert(D node) throws DataStreamException;
}
