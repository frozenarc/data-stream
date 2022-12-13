package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 */
public interface HasNextChecker {

    boolean hasNext() throws DataStreamException;
}
