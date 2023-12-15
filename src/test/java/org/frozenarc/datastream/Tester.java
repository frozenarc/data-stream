package org.frozenarc.datastream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.frozenarc.datastream.json.JsonStream;
import org.frozenarc.datastream.util.StackReader;
import org.frozenarc.datastream.util.StreamFetcher;
import org.junit.Test;

/**
 * Author: mpanchal
 * Date: 2023-12-15 00:26
 */
public class Tester {

    /*@Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonStream stream = new JsonStream(Tester.class.getResourceAsStream("/test.json"), mapper, 1);
        StreamFetcher<String, JsonNode> streamer = stream.streamFetcher()
                                                         .filter(n -> n.get("val").asInt() < 6)
                                                         .map(jsonNode -> {
                                                             try {
                                                                 return mapper.writeValueAsString(jsonNode);
                                                             } catch (JsonProcessingException e) {
                                                                 throw new RuntimeException(e);
                                                             }
                                                         })
                                                         .map(s -> !s.equals("null") ? s : "{ \"val\": 100 }")
                                                         .map(s -> {
                                                             try {
                                                                 return mapper.readTree(s);
                                                             } catch (JsonProcessingException e) {
                                                                 throw new RuntimeException(e);
                                                             }
                                                         });

        *//*while (streamer.hasNext()) {
            System.out.println(streamer.next().toPrettyString());
        }*//*
        *//*streamer.forEach(n -> {
            System.out.println(n.toPrettyString());
        });*//*
        *//*List<JsonNode> list = streamer.collectAsList();
        System.out.println(list);*//*
        // StackReader<JsonNode> stackReader = streamer.stackReader();
        while (streamer.hasNext()) {

            System.out.println(streamer.next().toPrettyString());
            System.out.println(streamer.stackReader().peek());
        }

    }

    @Test
    public void test2() throws DataStreamException {
        ObjectMapper mapper = new ObjectMapper();
        JsonStream stream = new JsonStream(Tester.class.getResourceAsStream("/test.json"), mapper, 1);
        StackReader<JsonNode> stackReader = stream.streamFetcher().stackReader();
        System.out.println(stackReader.pop());
        System.out.println(stackReader.pop());
        System.out.println(stackReader.peek());
        System.out.println(stackReader.peek());
        System.out.println(stackReader.pop());
        System.out.println(stackReader.peek());
        System.out.println(stackReader.pop());
    }*/
}
