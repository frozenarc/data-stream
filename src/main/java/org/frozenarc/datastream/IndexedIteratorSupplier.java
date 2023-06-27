package org.frozenarc.datastream;

/*
 * Author: mpanchal
 * Date: 15-02-2023
 */
public interface IndexedIteratorSupplier {

    void provides(IndexedIterator instance) throws DataStreamException;
}
