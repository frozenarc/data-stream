package org.frozenarc.datastream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.frozenarc.datastream.json.JsonStream;
import org.frozenarc.datastream.util.StackReader;
import org.frozenarc.datastream.util.StreamFetcher;
import org.junit.Test;

import java.util.Stack;

/**
 * Author: mpanchal
 * Date: 2023-12-15 00:26
 */
public class Tester {

    @Test
    public void test() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonStream stream = new JsonStream(Tester.class.getResourceAsStream("/test.json"), mapper, 1);
        StreamFetcher<String, JsonNode> streamFetcher = stream.streamFetcher()
                                                              .map(jsonNode -> {
                                                                  try {
                                                                      return mapper.writeValueAsString(jsonNode);
                                                                  } catch (JsonProcessingException e) {
                                                                      throw new RuntimeException(e);
                                                                  }
                                                              })
                                                              .map(s -> {
                                                                  try {
                                                                      return mapper.readTree(s);
                                                                  } catch (JsonProcessingException e) {
                                                                      throw new RuntimeException(e);
                                                                  }
                                                              });

        while (streamFetcher.hasNext()) {
            System.out.println(streamFetcher.next().toPrettyString());
        }
       /* streamFetcher.forEach(n -> {
            System.out.println(n.toPrettyString());
        });*/
        /*List<JsonNode> list = streamFetcher.collectAsList();
        System.out.println(list);*/
        // StackReader<JsonNode> stackReader = streamer.stackReader();
        /*while (streamFetcher.hasNext()) {

            System.out.println(streamFetcher.next().toPrettyString());
            System.out.println(streamFetcher.stackReader().peek());
        }*/

    }

    /*@Test
    public void test2() throws DataStreamException {
        ObjectMapper mapper = new ObjectMapper();
        JsonStream stream = new JsonStream(Tester.class.getResourceAsStream("/test.json"), mapper, 1);
        StackReader<JsonNode> stackReader = stream.streamFetcher().stackReader();
        System.out.println(stackReader.pop());
        System.out.println(stackReader.peek());
    }

    @Test
    public void test3() {
        Stack<Integer> stack = new Stack<>();
        stack.add(3);
        stack.add(2);
        stack.add(1);

        System.out.println(stack.peek());
        System.out.println(stack.pop());
    }*/
}
