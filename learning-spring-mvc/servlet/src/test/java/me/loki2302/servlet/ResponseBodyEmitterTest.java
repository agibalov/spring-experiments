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
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ResponseBodyEmitterTest {
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
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Autowired
        private Exchanger<Object> exchanger;

        @GetMapping
        public ResponseEntity<ResponseBodyEmitter> responseBodyEmitterHandler() {
            ResponseBodyEmitter responseBodyEmitter = new ResponseBodyEmitter();

            executorService.submit(() -> {
                try {
                    for(int i = 0; i < 3; ++i) {
                        exchanger.exchange(null);
                        responseBodyEmitter.send(String.format("hi there #%d\n", i));
                    }
                    responseBodyEmitter.complete();
                } catch (Throwable e) {
                    responseBodyEmitter.completeWithError(e);
                }
            });

            return ResponseEntity.ok(responseBodyEmitter);
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
