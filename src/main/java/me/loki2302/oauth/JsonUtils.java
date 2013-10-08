package me.loki2302.oauth;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class JsonUtils {
    private final static ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
    
    public static String asJson(Object o) {
        try {
            return objectWriter.writeValueAsString(o);
        } catch (JsonGenerationException e) {
            return "FAIL";
        } catch (JsonMappingException e) {
            return "FAIL";
        } catch (IOException e) {
            return "FAIL";
        }
    }
}