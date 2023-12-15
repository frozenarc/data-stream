package org.frozenarc.datastream.util;

/**
 * Author: mpanchal
 * Date: 2023-12-15 10:35
 */
public interface Stackable<T> {

    StackReader<T> stackReader();
}
