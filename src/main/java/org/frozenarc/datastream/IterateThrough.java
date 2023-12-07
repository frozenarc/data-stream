package org.frozenarc.datastream;

import org.frozenarc.datastream.iterators.HasNextChecker;
import org.frozenarc.datastream.iterators.NextFetcher;

/**
 * Author: mpanchal
 * Date: 12-03-2023 22:24
 */
public interface IterateThrough {

    int iterateThrough(HasNextChecker checker, NextFetcher fetcher, NextValidator validator) throws DataStreamException;
}
