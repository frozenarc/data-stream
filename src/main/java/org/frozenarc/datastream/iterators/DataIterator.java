package org.frozenarc.datastream.iterators;

import org.frozenarc.datastream.DataStreamException;
import org.frozenarc.datastream.consumers.DataConsumer;

/*
 * Author: mpanchal
 * Date: 17-08-2023
 */
public interface DataIterator<D> {

    void iterate(DataConsumer<D> consumer) throws DataStreamException;
}
