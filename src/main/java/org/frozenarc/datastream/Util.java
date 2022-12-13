package org.frozenarc.datastream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

/**
 * Author: mpanchal
 * Date: 01-11-2022
 * Utility class
 */
@SuppressWarnings("unused")
public class Util {

    /**
     * The method facilitates reading form data stream and write the to output stream as json array
     * @param outputStream to be written json array
     * @param checker checks whether data is available or not
     * @param fetcher fetches next available data
     * @param putArrayStartEnd true if start array "[" and end array "]" token needs to be write on output stream else false
     * @throws DataStreamException if anything goes wrong
     */
    public static void writeAsJsonArrayTo(OutputStream outputStream,
                                          HasNextChecker checker,
                                          NextFetcher fetcher,
                                          boolean putArrayStartEnd) throws DataStreamException {
        try {
            if (putArrayStartEnd) {
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
            if (putArrayStartEnd) {
                outputStream.write("]".getBytes());
            }
        } catch (IOException ex) {
            throw new DataStreamException(ex);
        }
    }

    /**
     * default implementation of HasNextChecker
     * @param dataStream to be checked on
     * @return default HasNextChecker
     * @param <T> node type
     */
    public static <T> HasNextChecker checker(DataStream<T> dataStream) {
        return dataStream::hasNext;
    }

    /**
     * default implementation of NextFetcher
     * @param dataStream to be worked on
     * @param convertor converts any kind of T into byte[]
     * @return default NextFetcher
     * @param <T> node type
     */
    public static <T> NextFetcher fetcher(DataStream<T> dataStream,
                                          Function<T, byte[]> convertor) {
        return () -> convertor.apply(dataStream.next());
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
     * @param nodes list of nodes
     * @param mapper ObjectMapper
     * @param putStartEnd true if start array "[" and end array "]" token needs to be write on output stream else false
     * @return byte[]
     * @throws DataStreamException if anything goes wrong
     */
    public static byte[] getJsonNodeBytes(List<JsonNode> nodes,
                                          ObjectMapper mapper,
                                          boolean putStartEnd) throws DataStreamException {
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
                           putStartEnd);
        return outputStream.toByteArray();
    }
}
