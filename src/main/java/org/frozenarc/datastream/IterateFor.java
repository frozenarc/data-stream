package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 12-03-2023 23:12
 */
public interface IterateFor<D> {

    int iterateFor(JsonBytesConvertor<D> convertor, NextValidator validator) throws DataStreamException;
}
