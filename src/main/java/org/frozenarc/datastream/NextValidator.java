package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 18-02-2023 21:15
 */
public interface NextValidator {

    boolean validate(byte[] data) throws DataStreamException;
}
