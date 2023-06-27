package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 16-02-2023 18:11
 */
public interface IndexedChecker {

    boolean check(int idx) throws DataStreamException;
}
