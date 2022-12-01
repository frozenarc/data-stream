package org.frozenarc.datastream;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:33
 */
public class DataStreamException extends Exception {

    public DataStreamException(String message) {
        super(message);
    }

    public DataStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataStreamException(Throwable cause) {
        super(cause);
    }
}
