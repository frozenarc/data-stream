package org.frozenarc.datastream.ext;

/**
 * Implement the interface to delegate stack reading functionality to StackReader
 * Author: mpanchal
 * Date: 2023-12-15 10:35
 */
public interface Stackable<T> {

    /**
     * Underlying implemented class should return StackReader
     * @return StackReader
     */
    StackReader<T> stackReader();
}
