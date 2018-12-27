package me.loki2302.servlet;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.concurrent.Exchanger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StreamingResponseBodyTest {
    @Autowired
    private Exchanger<Object> exchanger;

    @Test
    public void itShouldWork() throws IOException, InterruptedException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("http://localhost:8080/");
            try(CloseableHttpResponse response = httpClient.execute(request)) {
                try(InputStream is = response.getEntity().getContent()) {
                    try(BufferedReader responseBodyReader = new BufferedReader(new InputStreamReader(is))) {
                        for(int i = 0; i < 3; ++i) {
                            exchanger.exchange(null);
                            assertEquals(String.format("hi there #%d", i), responseBodyReader.readLine());
                        }

                        assertNull(responseBodyReader.readLine());
                    }
                }
            }
        }
    }

    @RestController
    public static class DummyController {
        @Autowired
        private Exchanger<Object> exchanger;

        @GetMapping
        public ResponseEntity<StreamingResponseBody> streamingResponseBodyHandler() {
            return ResponseEntity.ok(outputStream -> {
                // IMPORTANT: the client won't do the exchange() until it gets the
                // head of the response. Flushing the outputStream makes server send the head
                outputStream.flush();

                try(OutputStreamWriter osw = new OutputStreamWriter(outputStream)) {
                    for(int i = 0; i < 3; ++i) {
                        exchanger.exchange(null);
                        osw.write(String.format("hi there #%d\n", i));
                        osw.flush();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @SpringBootApplication
    public static class Config {
        @Bean
        public Exchanger<Object> exchanger() {
            return new Exchanger<>();
        }

        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }
}
