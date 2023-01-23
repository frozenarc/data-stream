package org.frozenarc.datastream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
     *
     * @param streamSupplier StreamSupplier
     * @param convertor JsonBytesConvertor to convert from D to byte[]
     * @param outputStream OutputStream to be written on
     * @param <D> type of node
     * @throws DataStreamException if anything goes wrong
     */
    public static <D> void handleDataStream(StreamSupplier<D> streamSupplier,
                                            JsonBytesConvertor<D> convertor,
                                            OutputStream outputStream) throws DataStreamException {

        DataStream<D> stream = streamSupplier.get();
        writeAsJsonArrayTo(outputStream,
                           checker(stream),
                           fetcher(stream, convertor),
                           true,
                           true);
    }

    /**
     *
     * @param streamSupplier StreamSupplier
     * @param convertor JsonBytesConvertor to convert from D to byte[]
     * @param outputStream OutputStream to be written on
     * @param putArrayStart true if start array "[" token needs to be written on output stream else false
     * @param putArrayEnd true if end array "]" token needs to be written on output stream else false
     * @param <D> type of node
     * @throws DataStreamException if anything goes wrong
     */
    public static <D> void handleDataStream(StreamSupplier<D> streamSupplier,
                                            JsonBytesConvertor<D> convertor,
                                            OutputStream outputStream,
                                            boolean putArrayStart,
                                            boolean putArrayEnd) throws DataStreamException {

        DataStream<D> stream = streamSupplier.get();
        writeAsJsonArrayTo(outputStream,
                           checker(stream),
                           fetcher(stream, convertor),
                           putArrayStart,
                           putArrayEnd);
    }

    /**
     * The method facilitates reading form data stream and write the to output stream as json array
     *
     * @param outputStream     to be written json array
     * @param checker          checks whether data is available or not
     * @param fetcher          fetches next available data
     * @param putArrayStart true if start array "[" token needs to be written on output stream else false
     * @param putArrayEnd true if end array "]" token needs to be written on output stream else false
     * @throws DataStreamException if anything goes wrong
     */
    public static void writeAsJsonArrayTo(OutputStream outputStream,
                                          HasNextChecker checker,
                                          NextFetcher fetcher,
                                          boolean putArrayStart,
                                          boolean putArrayEnd) throws DataStreamException {
        try {
            if (putArrayStart) {
                outputStream.write("[".getBytes());
            }
            boolean exceptFirst = false;
            while (checker.hasNext()) {
                if (exceptFirst) {
                    outputStream.write(",".getBytes());
                }
                exceptFirst = true;
                outputStream.write(fetcher.next());
            }
            if (putArrayEnd) {
                outputStream.write("]".getBytes());
            }
        } catch (IOException ex) {
            throw new DataStreamException(ex);
        }
    }

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
                                          JsonBytesConvertor<T> convertor) {
        return () -> convertor.convert(dataStream.next());
    }

    static class IndexHolder {
        int idx = 0;

        public int getIdx() {
            return idx;
        }

        public void setIdx(int idx) {
            this.idx = idx;
        }
    }

    /**
     * Converts node list into json array
     *
     * @param nodes       list of nodes
     * @param mapper      ObjectMapper
     * @param putArrayStart true if start array "[" token needs to be written on output stream else false
     * @param putArrayEnd true if end array "]" token needs to be written on output stream else false
     * @return byte[] converting list of node
     * @throws DataStreamException if anything goes wrong
     */
    public static byte[] getJsonNodeBytes(List<JsonNode> nodes,
                                          ObjectMapper mapper,
                                          boolean putArrayStart,
                                          boolean putArrayEnd) throws DataStreamException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IndexHolder holder = new IndexHolder();
        writeAsJsonArrayTo(outputStream,
                           () -> nodes.size() > holder.getIdx(),
                           () -> {
                               try {
                                   JsonNode node = nodes.get(holder.getIdx());
                                   return mapper.writeValueAsBytes(node);
                               } catch (JsonProcessingException e) {
                                   throw new DataStreamException(e);
                               } finally {
                                   holder.setIdx(holder.getIdx() + 1);
                               }
                           },
                           putArrayStart,
                           putArrayEnd);
        return outputStream.toByteArray();
    }
}
