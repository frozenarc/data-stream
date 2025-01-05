package org.frozenarc.datastream.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.frozenarc.datastream.DataStream;
import org.frozenarc.datastream.DataStreamException;
import org.frozenarc.datastream.ext.StreamFetcher;
import org.frozenarc.datastream.ext.Streamable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

public class JsonStreamPilot implements DataStream<JsonNode>, Streamable<JsonNode> {

    private final ObjectMapper mapper;
    private final JsonParser parser;
    private final StackManager stackManager;

    private JsonToken token;
    private boolean moreTokens = false;
    private boolean nextRead = true;

    private boolean valueOnly;

    /**
     * Constructor
     *
     * @param inputStream  to be read and get json nodes out of it
     * @param mapper       ObjectMapper
     * @param workingDepth at which depth interested nodes are in some array
     * @throws DataStreamException if any problem occurs
     */
    public JsonStreamPilot(InputStream inputStream, ObjectMapper mapper, int workingDepth) throws DataStreamException {
        this(inputStream, mapper, workingDepth, false);
    }

    /**
     * Constructor
     *
     * @param inputStream  to be read and get json nodes out of it
     * @param mapper       ObjectMapper
     * @param workingDepth at which depth interested nodes are in some array
     * @param valueOnly    Fetch only simple type values (e.g. string, number, boolean), no complex value (e.g. object, array)
     * @throws DataStreamException if any problem occurs
     */
    public JsonStreamPilot(InputStream inputStream, ObjectMapper mapper, int workingDepth, boolean valueOnly) throws DataStreamException {
        this.mapper = mapper;
        try {
            JsonFactory factory = new JsonFactory();
            parser = factory.createParser(inputStream);
            stackManager = new StackManager(workingDepth);
            this.valueOnly = valueOnly;
        } catch (IOException e) {
            throw new DataStreamException(e);
        }
    }

    @Override
    public boolean hasNext() throws DataStreamException {
        if (token == JsonToken.NOT_AVAILABLE) {
            throw new DataStreamException("Input stream is closed");
        }
        try {
            if (nextRead) {
                nextRead = false;
                moreTokens = false;
                while (!(stackManager.isStackEmpty() && token != null)
                       && stackManager.getDepth() < stackManager.getWorkingDepth()) {
                    token = pickNextToken();
                    if (isStartToken() && stackManager.isOnWorkingDepth()) {
                        moreTokens = true;
                    }
                    if (isEndToken() && stackManager.getDepth() < stackManager.getWorkingDepth()) {
                        moreTokens = false;
                    }
                }
            }
            return moreTokens;
        } catch (Exception ex) {
            throw new DataStreamException(ex);
        }
    }

    public boolean isStartToken() {
        return token == JsonToken.START_ARRAY || token == JsonToken.START_OBJECT;
    }

    public boolean isEndToken() {
        return token == JsonToken.END_ARRAY || token == JsonToken.END_OBJECT;
    }

    @Override
    public JsonNode next() throws DataStreamException {
        try {
            if (!isStartToken()) {
                throw new IllegalStateException("Current token is not a START token, Token: " + token);
            }
            nextRead = true;
            Stack<JsonNode> stack = new Stack<>();
            while (!isEndToken() || stackManager.getDepth() >= stackManager.getWorkingDepth()) {
                if (token == JsonToken.START_ARRAY) {
                    if (stack.isEmpty()) {
                        stack.push(mapper.createArrayNode());
                    } else {
                        if (valueOnly) {
                            while (!(token == JsonToken.END_ARRAY && stackManager.getDepth() == stackManager.getWorkingDepth())) {
                                token = pickNextToken();
                            }
                        } else {
                            if (parser.getCurrentName() != null) {
                                ((ObjectNode) stack.peek()).set(parser.getCurrentName(), stack.push(mapper.createArrayNode()));
                            } else {
                                ((ArrayNode) stack.peek()).add(stack.push(mapper.createArrayNode()));
                            }
                        }
                    }
                }
                if (token == JsonToken.START_OBJECT) {
                    if (stack.isEmpty()) {
                        stack.push(mapper.createObjectNode());
                    } else {
                        if (valueOnly) {
                            //System.out.println(stackManager.getDepth() + ": " + stackManager.getWorkingDepth() + ": " + token + ": " + parser.getCurrentName());
                            while (!(token == JsonToken.END_OBJECT && stackManager.getDepth() == stackManager.getWorkingDepth())) {
                                //System.out.println(token == JsonToken.END_OBJECT && stackManager.getDepth() < stackManager.getWorkingDepth());
                                token = pickNextToken();
                                System.out.println(stackManager.getDepth() + ": " + stackManager.getWorkingDepth() + ": " + token + ": " + parser.getCurrentName());
                            }
                            //System.out.println(stackManager.getDepth() + ": " + stackManager.getWorkingDepth() + ": " + token + ": " + parser.getCurrentName());
                        } else {
                            if (parser.getCurrentName() != null) {
                                ((ObjectNode) stack.peek()).set(parser.getCurrentName(), stack.push(mapper.createObjectNode()));
                            } else {
                                ((ArrayNode) stack.peek()).add(stack.push(mapper.createObjectNode()));
                            }
                        }
                    }
                }
                if (isEndToken() && stackManager.getDepth() >= stackManager.getWorkingDepth()) {
                    stack.pop();
                }
                if (isValueToken()) {
                    setValueFor(parser.getCurrentName(), stack.peek());
                }
                token = pickNextToken();
            }
            return stack.pop();
        } catch (Exception ex) {
            throw new DataStreamException(ex);
        }
    }

    @Override
    public StreamFetcher<JsonNode, JsonNode> streamFetcher() {
        return new StreamFetcher<>(this);
    }

    private boolean isValueToken() {
        return token == JsonToken.VALUE_STRING
               || token == JsonToken.VALUE_NUMBER_INT
               || token == JsonToken.VALUE_NUMBER_FLOAT
               || token == JsonToken.VALUE_TRUE
               || token == JsonToken.VALUE_FALSE
               || token == JsonToken.VALUE_NULL;
    }

    private JsonToken pickNextToken() throws IOException {
        JsonToken token = parser.nextToken();
        if (token == null) {
            token = JsonToken.NOT_AVAILABLE;
        }
        stackManager.manage(token);
        return token;
    }

    private void setValueFor(String key, JsonNode node) throws IOException {
        if (key != null && node instanceof ObjectNode) {
            if (token == JsonToken.VALUE_STRING) {
                ((ObjectNode) node).put(key, parser.getValueAsString());
            }
            if (token == JsonToken.VALUE_NUMBER_INT) {
                ((ObjectNode) node).put(key, parser.getValueAsLong());
            }
            if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                ((ObjectNode) node).put(key, parser.getValueAsDouble());
            }
            if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                ((ObjectNode) node).put(key, parser.getValueAsBoolean());
            }
            if (token == JsonToken.VALUE_NULL) {
                ((ObjectNode) node).putNull(key);
            }
        }
        if (key == null && node instanceof ArrayNode) {
            if (token == JsonToken.VALUE_STRING) {
                ((ArrayNode) node).add(parser.getValueAsString());
            }
            if (token == JsonToken.VALUE_NUMBER_INT) {
                ((ArrayNode) node).add(parser.getValueAsLong());
            }
            if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                ((ArrayNode) node).add(parser.getValueAsDouble());
            }
            if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                ((ArrayNode) node).add(parser.getValueAsBoolean());
            }
            if (token == JsonToken.VALUE_NULL) {
                ((ArrayNode) node).addNull();
            }
        }
    }
}