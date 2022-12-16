package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 * Interface to represent HasNextChecker of any kind
 */
public interface HasNextChecker {

    /**
     * Represents hasNext of any custom kind
     *
     * @return true or false as per next node availability
     * @throws DataStreamException if some problem occurs
     */
    boolean hasNext() throws DataStreamException;
}
