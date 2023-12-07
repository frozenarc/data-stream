package org.frozenarc.datastream.validators;

import org.frozenarc.datastream.DataStreamException;

/*
 * Author: mpanchal
 * Date: 04-10-2023
 */
public interface DataValidator<D> {

    boolean validate(D node) throws DataStreamException;
}
