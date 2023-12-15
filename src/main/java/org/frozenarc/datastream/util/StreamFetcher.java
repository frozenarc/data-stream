package org.frozenarc.datastream.util;

import org.frozenarc.datastream.DataStream;
import org.frozenarc.datastream.DataStreamException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Author: mpanchal
 * Date: 2023-12-14 23:14
 */
public class StreamFetcher<T, U> implements DataStream<U>, Stackable<U> {

    private final DataStream<T> stream;
    private Function<T, U> mapper;
    private Predicate<T> predicate;
    private U last;

    public StreamFetcher(DataStream<T> stream,
                         Function<T, U> mapper) {
        this.stream = stream;
        this.mapper = mapper;
    }

    public StreamFetcher(DataStream<T> stream,
                         Predicate<T> predicate) {
        this.stream = stream;
        this.predicate = predicate;
    }

    public StreamFetcher(DataStream<T> stream) {
        this.stream = stream;
    }

    @Override
    public boolean hasNext() throws DataStreamException {
        return stream.hasNext();
    }

    @Override
    public U next() throws DataStreamException {
        if (mapper != null) {
            return last = mapper.apply(stream.next());
        } else if (predicate != null) {
            do {
                T val = stream.next();
                if (predicate.test(val)) {
                    //noinspection unchecked
                    return last = (U) val;
                }
            } while (stream.hasNext());
            return null;
        } else {
            //noinspection unchecked
            return last = (U) stream.next();
        }
    }

    public <V> StreamFetcher<U, V> map(Function<U, V> mapper) {
        return new StreamFetcher<>(this, mapper);
    }

    public StreamFetcher<U, U> filter(Predicate<U> predicate) {
        return new StreamFetcher<>(this, predicate);
    }

    public void forEach(Consumer<U> consumer) throws DataStreamException {
        while (hasNext()) {
            consumer.accept(next());
        }
    }

    public List<U> collectAsList() throws DataStreamException {
        List<U> list = new ArrayList<>();
        while (hasNext()) {
            list.add(next());
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public StackReader<U> stackReader() {
        return new StackReader<>((StreamFetcher<U, U>) this);
    }

    U getLast() {
        return last;
    }
}
