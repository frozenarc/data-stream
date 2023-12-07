package org.frozenarc.datastream.consumers;

import org.frozenarc.datastream.DataStreamException;

/*
 * Author: mpanchal
 * Date: 17-08-2023
 */
public interface DataConsumer<D> {

    void startConsuming() throws DataStreamException;

    void consume(D data) throws DataStreamException;

    void endConsuming() throws DataStreamException;
}
