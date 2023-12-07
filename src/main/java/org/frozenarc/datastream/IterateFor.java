package org.frozenarc.datastream;

import org.frozenarc.datastream.convertors.DataConvertor;

/**
 * Author: mpanchal
 * Date: 12-03-2023 23:12
 */
public interface IterateFor<D> {

    int iterateFor(DataConvertor<D> convertor, NextValidator validator) throws DataStreamException;
}
