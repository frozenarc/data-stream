package org.frozenarc.datastream.ext;

import org.frozenarc.datastream.DataStream;
import org.frozenarc.datastream.DataStreamException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Author: mpanchal
 * Date: 2023-12-14 23:14
 * provides support of map, forEach, collect
 */
public class StreamFetcher<T, U> implements DataStream<U>, Stackable<U> {

    private final DataStream<T> stream;
    private Function<T, U> mapper;
    private U last;

    public StreamFetcher(DataStream<T> stream,
                         Function<T, U> mapper) {
        this.stream = stream;
        this.mapper = mapper;
    }

    public StreamFetcher(DataStream<T> stream) {
        this.stream = stream;
    }

    /**
     * calls hasNext of underlying stream
     * @return true or false
     * @throws DataStreamException thrown if anything gets wrong
     */
    @Override
    public boolean hasNext() throws DataStreamException {
        return stream.hasNext();
    }

    /**
     * calls next of underlying stream
     * @return next node from stream
     * @throws DataStreamException thrown if anything gets wrong
     */
    @Override
    public U next() throws DataStreamException {
        if (mapper != null) {
            return last = mapper.apply(stream.next());
        } else {
            //noinspection unchecked
            return last = (U) stream.next();
        }
    }

    /**
     * Accepts mapper function to work on it
     * @param mapper function
     * @return StreamFetcher to work next
     */
    @SuppressWarnings("unused")
    public <V> StreamFetcher<U, V> map(Function<U, V> mapper) {
        return new StreamFetcher<>(this, mapper);
    }

    /**
     * Accepts consumer to be called for each node
     * @param consumer function
     * @throws DataStreamException thrown if anything gets wrong
     */
    @SuppressWarnings("unused")
    public void forEach(Consumer<U> consumer) throws DataStreamException {
        while (hasNext()) {
            consumer.accept(next());
        }
    }

    /**
     * Can collect stream data as list
     * @return List of the collected data
     * @throws DataStreamException thrown if anything gets wrong
     */
    @SuppressWarnings("unused")
    public List<U> collectAsList() throws DataStreamException {
        List<U> list = new ArrayList<>();
        while (hasNext()) {
            list.add(next());
        }
        return list;
    }

    /**
     * Returns StackReader to work on stream as stack way
     * @return StackReader
     */
    @SuppressWarnings("unchecked")
    @Override
    public StackReader<U> stackReader() {
        return new StackReader<>((StreamFetcher<U, U>) this);
    }

    U getLast() {
        return last;
    }
}
