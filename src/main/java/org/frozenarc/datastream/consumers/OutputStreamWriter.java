package org.frozenarc.datastream.consumers;

import org.frozenarc.datastream.DataStreamException;
import org.frozenarc.datastream.convertors.DataConvertor;
import org.frozenarc.datastream.validators.DataValidator;

import java.io.IOException;
import java.io.OutputStream;

/*
 * Author: mpanchal
 * Date: 17-08-2023
 */
public class OutputStreamWriter<D> implements DataConsumer<D> {

    private final OutputStream outputStream;
    private final DataValidator<D> validator;
    private final DataConvertor<D> convertor;

    private final boolean putArrayStart;
    private final boolean putArrayEnd;
    private final boolean firstBatch;

    private boolean firstNode = true;

    private final int flushLimit;
    private int recordCount = 0;

    public OutputStreamWriter(OutputStream outputStream,
                              DataValidator<D> validator,
                              DataConvertor<D> convertor,
                              boolean putArrayStart,
                              boolean putArrayEnd,
                              boolean firstBatch) {

        this(outputStream, validator, convertor, putArrayStart, putArrayEnd, firstBatch, 100);
    }

    public OutputStreamWriter(OutputStream outputStream,
                              DataValidator<D> validator,
                              DataConvertor<D> convertor,
                              boolean putArrayStart,
                              boolean putArrayEnd,
                              boolean firstBatch,
                              int flushLimit) {

        this.outputStream = outputStream;
        this.validator = validator;
        this.convertor = convertor;

        this.putArrayStart = putArrayStart;
        this.putArrayEnd = putArrayEnd;
        this.firstBatch = firstBatch;

        this.flushLimit = flushLimit;
    }

    @Override
    public void startConsuming() throws DataStreamException {
        if (putArrayStart) {
            try {
                outputStream.write("[".getBytes());
            } catch (IOException ex) {
                throw new DataStreamException(ex);
            }
        }
    }

    @Override
    public void consume(D data) throws DataStreamException {
        try {
            if (validator.validate(data)) {
                if (!firstNode || !firstBatch) {
                    outputStream.write(",".getBytes());
                }
                firstNode = false;
                outputStream.write(convertor.convert(data));
                recordCount = recordCount + 1;
                if(flushLimit == recordCount) {
                    outputStream.flush();
                }
            }
        } catch (IOException ex) {
            throw new DataStreamException(ex);
        }
    }

    @Override
    public void endConsuming() throws DataStreamException {
        if (putArrayEnd) {
            try {
                outputStream.write("]".getBytes());
                outputStream.flush();
            } catch (IOException ex) {
                throw new DataStreamException(ex);
            }
        }
    }
}
