package me.loki2302.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static org.junit.Assert.assertEquals;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class PatchHttpMethodTest {
    @Autowired
    private TodoController todoController;

    @Test
    public void canUsePatchMethod() {
        TodoDto originalTodo = new TodoDto();
        originalTodo.title = "title";
        originalTodo.text = "text";
        originalTodo.tags = new HashSet<>(Arrays.asList("todo", "not-today", "maybe"));
        todoController.persistedTodo = originalTodo;

        Map<String, Object> patchBody = new HashMap<>();
        patchBody.put("title", "new title");
        patchBody.put("tags", Arrays.asList("don't-do"));
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        RequestEntity<?> requestEntity = RequestEntity
                .patch(URI.create("http://localhost:8080/todo"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body("{\n" +
                        "    \"title\": \"new title\",\n" +
                        "    \"tags\": [\"don't-do\"]\n" +
                        "}");

        restTemplate.exchange(requestEntity, Void.class);

        assertEquals("new title", todoController.persistedTodo.title);
        assertEquals("text", todoController.persistedTodo.text);
        assertEquals(new HashSet<>(Arrays.asList("don't-do")), todoController.persistedTodo.tags);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public TodoController dummyController() {
            return new TodoController();
        }
    }

    @RestController
    @RequestMapping("/todo")
    public static class TodoController {
        public TodoDto persistedTodo;

        @Autowired
        private ObjectMapper objectMapper;

        @PutMapping
        public ResponseEntity<?> putTodo(@RequestBody TodoDto todoDto) {
            persistedTodo = todoDto;
            return ResponseEntity.noContent().build();
        }

        @PatchMapping
        public ResponseEntity<?> patchTodo(@RequestBody JsonNode jsonNode) {
            try {
                objectMapper.readerForUpdating(persistedTodo).readValue(jsonNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return ResponseEntity.noContent().build();
        }
    }

    public static class TodoDto {
        public String title;
        public String text;
        public Set<String> tags;
    }
}
