package org.frozenarc.datastream.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.frozenarc.datastream.DataStreamException;
import org.frozenarc.datastream.convertors.DataConvertor;
import org.frozenarc.datastream.validators.DataValidator;

import java.io.OutputStream;

/*
 * Author: mpanchal
 * Date: 17-08-2023
 */
public class JsonNodeToOutputStream extends OutputStreamWriter<JsonNode> {

    public JsonNodeToOutputStream(OutputStream outputStream,
                                  DataValidator<JsonNode> validator,
                                  ObjectMapper mapper,
                                  boolean putArrayStart, boolean putArrayEnd,
                                  boolean firstBatch) {
        super(outputStream,
              validator,
              convertor(mapper),
              putArrayStart,
              putArrayEnd,
              firstBatch);
    }

    public static DataConvertor<JsonNode> convertor(ObjectMapper mapper) {
        return node -> {
            try {
                return mapper.writeValueAsBytes(node);
            } catch (JsonProcessingException ex) {
                throw new DataStreamException(ex);
            }
        };
    }
}
