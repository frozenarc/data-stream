package org.frozenarc.datastream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.frozenarc.datastream.convertors.DataConvertor;
import org.frozenarc.datastream.iterators.DataIterator;
import org.frozenarc.datastream.iterators.HasNextChecker;
import org.frozenarc.datastream.iterators.NextFetcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 * Utility class
 */
@SuppressWarnings("unused")
public class DataStreamUtil {

    /**
     * default implementation of HasNextChecker
     *
     * @param dataStream to be checked on
     * @param <T>        node type
     * @return default HasNextChecker
     */
    public static <T> HasNextChecker checker(DataStream<T> dataStream) {
        return dataStream::hasNext;
    }

    /**
     * default implementation of NextFetcher
     *
     * @param dataStream to be worked on
     * @param convertor  converts any kind of T into byte[]
     * @param <T>        node type
     * @return default NextFetcher
     */
    public static <T> NextFetcher fetcher(DataStream<T> dataStream,
                                          DataConvertor<T> convertor) {
        return () -> convertor.convert(dataStream.next());
    }

    public static NextValidator validator() {
        return data -> data != null && data.length > 0;
    }

    /**
     * Converts node list into json array
     *
     * @param nodes              list of nodes
     * @param mapper             ObjectMapper
     * @param putArrayStart      true if start array "[" token needs to be written on output stream else false
     * @param putArrayEnd        true if end array "]" token needs to be written on output stream else false
     * @param noCommaBeforeBatch true if first batch is being written on stream or there is only one batch to be written
     * @return byte[] converting list of node
     * @throws DataStreamException if anything goes wrong
     */
    public static byte[] getJsonNodeBytes(List<JsonNode> nodes,
                                          ObjectMapper mapper,
                                          boolean putArrayStart,
                                          boolean putArrayEnd,
                                          boolean noCommaBeforeBatch) throws DataStreamException {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        IndexedIterator.instanceSupplier((instance) -> writeAsJsonArrayTo(outputStream,
                                                                          putArrayStart,
                                                                          putArrayEnd,
                                                                          noCommaBeforeBatch)
                .iterateThrough(instance.checker(idx -> idx < nodes.size()),
                                instance.fetcher(idx -> {
                                    try {
                                        JsonNode node = nodes.get(idx);
                                        return mapper.writeValueAsBytes(node);
                                    } catch (JsonProcessingException e) {
                                        throw new DataStreamException(e);
                                    }
                                }),
                                validator()));

        return outputStream.toByteArray();
    }

    public static IterateThrough writeAsJsonArrayTo(OutputStream outputStream,
                                                    boolean putArrayStart,
                                                    boolean putArrayEnd,
                                                    boolean noCommaBeforeBatch) {
        return (checker, fetcher, validator) -> {
            int count = 0;
            try {
                if (putArrayStart) {
                    outputStream.write("[".getBytes());
                }
                int flushCount = 1;
                boolean putCommaBeforeNode = !noCommaBeforeBatch;
                while (checker.hasNext()) {
                    byte[] data = fetcher.next();
                    if (validator.validate(data)) {
                        if (putCommaBeforeNode) {
                            outputStream.write(",".getBytes());
                        }
                        putCommaBeforeNode = true;
                        outputStream.write(data);
                        if (flushCount > 100) {
                            outputStream.flush();
                            flushCount = 0;
                        }
                        flushCount++;
                        count++;
                    }
                }
                if (putArrayEnd) {
                    outputStream.write("]".getBytes());
                }
                outputStream.flush();
            } catch (IOException ex) {
                throw new DataStreamException(ex);
            }
            return count;
        };
    }

    public static <D> IterateFor<D> handleDataStream(OutputStream outputStream,
                                                     StreamSupplier<D> streamSupplier,
                                                     boolean putArrayStart,
                                                     boolean putArrayEnd,
                                                     boolean noCommaBeforeBatch) throws DataStreamException {

        DataStream<D> stream = streamSupplier.get();

        IterateThrough iterateThrough = writeAsJsonArrayTo(outputStream,
                                                           putArrayStart,
                                                           putArrayEnd,
                                                           noCommaBeforeBatch);

        return (convertor, validator) -> iterateThrough.iterateThrough(checker(stream), fetcher(stream, convertor), validator);
    }

    public static <D> DataIterator<D> handle(DataStream<D> stream) {
        return (consumer) -> {
            consumer.startConsuming();
            while (stream.hasNext()) {
                D data = stream.next();
                consumer.consume(data);
            }
            consumer.endConsuming();
        };
    }

}
