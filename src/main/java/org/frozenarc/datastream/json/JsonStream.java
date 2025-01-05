package org.frozenarc.datastream.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.frozenarc.datastream.DataStream;
import org.frozenarc.datastream.DataStreamException;
import org.frozenarc.datastream.ext.StreamFetcher;
import org.frozenarc.datastream.ext.Streamable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Author: mpanchal
 * Date: 2022-12-01 06:35
 * Class to handle json data stream
 */
@SuppressWarnings("unused")
public class JsonStream implements DataStream<JsonNode>, Streamable<JsonNode> {

    private final ObjectMapper mapper;
    private final String targetField;

    private final JsonParser parser;
    private final StackManager stackManager;

    private JsonToken token;
    private boolean moreTokens = false;
    private boolean nextRead = true;
    private String fieldOnTargetDepth;

    /**
     * Constructor
     *
     * @param inputStream  to be read and get json nodes out of it
     * @param mapper       ObjectMapper
     * @param workingDepth at which depth interested nodes are in some array
     * @throws DataStreamException if any problem occurs
     */
    public JsonStream(InputStream inputStream, ObjectMapper mapper, int workingDepth) throws DataStreamException {
        this(inputStream, mapper, workingDepth, null);
    }

    /**
     * Constructor
     *
     * @param inputStream  to be read and get json nodes out of it
     * @param mapper       ObjectMapper
     * @param workingDepth at which depth interested nodes are in some array
     * @param targetField  if multiple parent fields exists at working depth, but we are interested to only this one
     * @throws DataStreamException if any problem occurs
     */
    public JsonStream(InputStream inputStream, ObjectMapper mapper, int workingDepth, String targetField) throws DataStreamException {
        this.mapper = mapper;
        this.targetField = targetField;
        try {
            JsonFactory factory = new JsonFactory();
            parser = factory.createParser(inputStream);
            stackManager = new StackManager(workingDepth);
        } catch (IOException e) {
            throw new DataStreamException(e);
        }
    }

    /**
     * to be used to check whether next node is available or not
     *
     * @return true if node available to be read or false
     * @throws DataStreamException if any problem occurs
     */
    public boolean hasNext() throws DataStreamException {
        try {
            if (nextRead) {
                nextRead = false;
                while (!(shouldParsingStart() || shouldParsingEnd())) {

                    token = pickNextToken();
                    if (token == JsonToken.FIELD_NAME && stackManager.isLessThanWorkingDepthBy(2)) {
                        fieldOnTargetDepth = parser.getCurrentName();
                    }
                }
                if (shouldParsingEnd()) {
                    moreTokens = false;
                }
                if (shouldParsingStart()) {
                    moreTokens = true;
                }
            }
            return moreTokens;
        } catch (IOException ex) {
            throw new DataStreamException(ex);
        }
    }

    /**
     * @return next node
     * @throws DataStreamException if any problem occurs
     */
    public JsonNode next() throws DataStreamException {
        try {
            if (token != JsonToken.START_OBJECT) {
                throw new IllegalStateException("Current token is not a START token, Token: " + token);
            }
            nextRead = true;
            String key = null;
            ObjectNode node = null;
            while (token != JsonToken.END_OBJECT) {
                if (token == JsonToken.START_OBJECT) {
                    if (node == null) {
                        node = mapper.createObjectNode();
                    } else {
                        node.set(key, next());
                    }
                }
                if (token == JsonToken.FIELD_NAME) {
                    key = parser.getCurrentName();
                }
                setValueFor(key, node);
                token = pickNextToken();
            }
            return node;
        } catch (IOException ex) {
            throw new DataStreamException(ex);
        }
    }

    /**
     * Returns StreamFetcher as wrapper of the stream
     * @return StreamFetcher
     */
    public StreamFetcher<JsonNode, JsonNode> streamFetcher() {
        return new StreamFetcher<>(this);
    }

    private JsonToken pickNextToken() throws IOException {
        JsonToken token = parser.nextToken();
        if(token == null) {
            token = JsonToken.NOT_AVAILABLE;
        }
        stackManager.manage(token);
        return token;
    }

    private boolean shouldParsingStart() throws DataStreamException {
        if(token == JsonToken.NOT_AVAILABLE) {
            throw new DataStreamException("InputStream is closed");
        }
        return token == JsonToken.START_OBJECT
               && stackManager.isOnWorkingDepth()
               && (targetField == null || _isFieldFound());
    }

    private boolean shouldParsingEnd() throws DataStreamException {
        if(token == JsonToken.NOT_AVAILABLE) {
            throw new DataStreamException("InputStream is closed");
        }
        return (token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT)
               && stackManager.isStackEmpty();
    }

    private boolean _isFieldFound() {
        if (fieldOnTargetDepth == null) {
            throw new IllegalStateException("Target field seems deeper than defined depth");
        }
        return fieldOnTargetDepth.equals(targetField);
    }

    private void setValueFor(String key, ObjectNode node) throws IOException {
        if (token == JsonToken.VALUE_STRING) {
            node.put(key, parser.getValueAsString());
        }
        if (token == JsonToken.VALUE_NUMBER_INT) {
            node.put(key, parser.getValueAsLong());
        }
        if (token == JsonToken.VALUE_NUMBER_FLOAT) {
            node.put(key, parser.getValueAsDouble());
        }
        if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
            node.put(key, parser.getValueAsBoolean());
        }
        if (token == JsonToken.VALUE_NULL) {
            node.putNull(key);
        }
    }
}
