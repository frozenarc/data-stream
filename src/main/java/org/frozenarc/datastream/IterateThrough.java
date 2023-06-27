package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 12-03-2023 22:24
 */
public interface IterateThrough {

    int iterateThrough(HasNextChecker checker, NextFetcher fetcher, NextValidator validator) throws DataStreamException;
}
