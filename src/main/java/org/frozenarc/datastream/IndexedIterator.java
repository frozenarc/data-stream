package org.frozenarc.datastream;

import org.frozenarc.datastream.iterators.HasNextChecker;
import org.frozenarc.datastream.iterators.NextFetcher;

/*
 * Author: mpanchal
 * Date: 15-02-2023
 */
public class IndexedIterator {

    int idx = 0;

    public static void instanceSupplier(IndexedIteratorSupplier supplier) throws DataStreamException {
        supplier.provides(new IndexedIterator());
    }

    public HasNextChecker checker(IndexedChecker checker) {
        return () -> checker.check(idx);
    }

    public NextFetcher fetcher(IndexedFetcher fetcher) {
        return () -> {
            try {
                return fetcher.fetch(idx);
            } finally {
                idx++;
            }
        };
    }

    public int getIdx() {
        return this.idx;
    }
}
