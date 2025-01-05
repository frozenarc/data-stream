package org.frozenarc.datastream.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonStreamUtil {


    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static long transferTo(InputStream in, OutputStream out) throws IOException {
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            transferred += read;
        }
        return transferred;
    }

    /*public static void streamJson(InputStream inputStream, OutputStream outputStream) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(inputStream);
        JsonGenerator generator = factory.createGenerator(outputStream);
        JsonToken token;
        JsonToken lastToken = null;
        while ((token = parser.nextToken()) != null) {
            if(token == JsonToken.START_ARRAY) {
                if(parser.getCurrentName() != null) {
                    generator.writeArrayFieldStart(parser.getCurrentName());
                } else {
                    generator.writeStartArray();
                }
                lastToken = token;
            }
            if(token == JsonToken.END_ARRAY) {
                generator.writeEndArray();
                lastToken = token;
            }
            if(token == JsonToken.START_OBJECT) {
                if(parser.getCurrentName() != null) {
                    generator.writeObjectFieldStart(parser.getCurrentName());
                } else {
                    generator.writeStartObject();
                }
                lastToken = token;
            }
            if(token == JsonToken.END_OBJECT) {
                generator.writeEndObject();
                lastToken = token;
            }
            if(isValueToken(token)) {
                setValueFor(token, lastToken, generator, parser.getCurrentName(), parser);
            }
        }
        generator.flush();
    }*/

    private static boolean isValueToken(JsonToken token) {
        return token == JsonToken.VALUE_STRING
               || token == JsonToken.VALUE_NUMBER_INT
               || token == JsonToken.VALUE_NUMBER_FLOAT
               || token == JsonToken.VALUE_TRUE
               || token == JsonToken.VALUE_FALSE
               || token == JsonToken.VALUE_NULL;
    }

    private static void setValueFor(JsonToken token, JsonToken lastToken, JsonGenerator generator, String key, JsonParser parser) throws IOException {
        if (key != null && lastToken == JsonToken.START_OBJECT) {
            if (token == JsonToken.VALUE_STRING) {
                generator.writeStringField(key, parser.getValueAsString());
            }
            if (token == JsonToken.VALUE_NUMBER_INT) {
                generator.writeNumberField(key, parser.getValueAsLong());
            }
            if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                generator.writeNumberField(key, parser.getValueAsDouble());
            }
            if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                generator.writeBooleanField(key, parser.getValueAsBoolean());
            }
            if (token == JsonToken.VALUE_NULL) {
                generator.writeNullField(key);
            }
        }
        if (key == null && lastToken == JsonToken.START_ARRAY) {
            if (token == JsonToken.VALUE_STRING) {
                generator.writeString(parser.getValueAsString());
            }
            if (token == JsonToken.VALUE_NUMBER_INT) {
                generator.writeNumber(parser.getValueAsLong());
            }
            if (token == JsonToken.VALUE_NUMBER_FLOAT) {
                generator.writeNumber(parser.getValueAsDouble());
            }
            if (token == JsonToken.VALUE_TRUE || token == JsonToken.VALUE_FALSE) {
                generator.writeBoolean(parser.getValueAsBoolean());
            }
            if (token == JsonToken.VALUE_NULL) {
                generator.writeNull();
            }
        }
    }
}
