package me.loki2302.reactive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.server.WebServerException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.*;
import org.springframework.lang.Nullable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Inspired by https://github.com/awslabs/aws-serverless-java-container/tree/master/aws-serverless-java-container-spring
 * The goal is to see if things get easier if I don't need to make it emulate a servlet container.
 *
 * @see me.loki2302.servlet.dummysc.DummyServletContainerTest
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class CustomWebServerTest {
    @Autowired
    private HttpHandler httpHandler;

    @Test
    public void dummy() {
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory(false);

        ServerHttpRequest request = new DummyServerHttpRequest(
                URI.create("http://localhost:8080/"),
                null,
                new HttpHeaders() {{
                    setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    setOrigin("http://www.google.com/");
                }},
                "GET",
                bufferFactory.wrap(new byte[] {}));

        DummyServerHttpResponse response = new DummyServerHttpResponse(bufferFactory);
        httpHandler.handle(request, response).block();
        System.out.println(response);

        HttpStatus httpStatus = response.getStatusCode();
        assertEquals(HttpStatus.OK, httpStatus);

        HttpHeaders httpHeaders = response.getHeaders();
        assertTrue(httpHeaders.containsKey(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));

        MultiValueMap<String, ResponseCookie> cookies = response.getCookies();
        assertTrue(cookies.isEmpty());

        assertTrue(response.getBody().contains("{\"message\":\"hello world"));
    }

    @SpringBootApplication(exclude = {ReactiveWebServerFactoryAutoConfiguration.class})
    public static class Config {
        @Bean
        public ReactiveWebServerFactory reactiveWebServerFactory() {
            return new DummyReactiveWebServerFactory();
        }

        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }

    @RestController
    @CrossOrigin
    public static class DummyController {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyController.class);

        @GetMapping("/")
        @ResponseStatus(HttpStatus.OK)
        public DataDto test() {
            LOGGER.info("test called");
            DataDto dataDto = new DataDto();
            dataDto.message = String.format("hello world %s", new Date());
            return dataDto;
        }
    }

    public static class DataDto {
        public String message;
    }

    public static class DummyReactiveWebServerFactory implements ReactiveWebServerFactory {
        @Override
        public WebServer getWebServer(HttpHandler httpHandler) {
            return new DummyWebServer();
        }
    }

    public static class DummyWebServer implements WebServer {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyWebServer.class);

        @Override
        public void start() throws WebServerException {
            LOGGER.info("start()");
        }

        @Override
        public void stop() throws WebServerException {
            LOGGER.info("stop()");
        }

        @Override
        public int getPort() {
            LOGGER.info("getPort()");
            return -1;
        }
    }

    public static class DummyServerHttpRequest extends AbstractServerHttpRequest {
        private final String method;
        private final DataBuffer body;

        public DummyServerHttpRequest(
                URI uri,
                @Nullable String contextPath,
                HttpHeaders headers,
                String method,
                DataBuffer body) {

            super(uri, contextPath, headers);
            this.method = method;
            this.body = body;
        }

        @Override
        protected MultiValueMap<String, HttpCookie> initCookies() {
            return new LinkedMultiValueMap<>();
        }

        @Override
        protected SslInfo initSslInfo() {
            return null;
        }

        @Override
        public <T> T getNativeRequest() {
            return (T)this;
        }

        @Nullable
        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getMethodValue() {
            return method;
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return Flux.just(body);
        }
    }

    public static class DummyServerHttpResponse extends AbstractServerHttpResponse {
        private final static Logger LOGGER = LoggerFactory.getLogger(DummyServerHttpResponse.class);
        private String body;

        public DummyServerHttpResponse(DataBufferFactory dataBufferFactory) {
            super(dataBufferFactory);
        }

        @Override
        public <T> T getNativeResponse() {
            return (T) this;
        }

        @Override
        protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> bodyPublisher) {
            return Mono.create(sink -> {
                bodyPublisher.subscribe(new BaseSubscriber<DataBuffer>() {
                    @Override
                    protected void hookOnNext(DataBuffer value) {
                        byte data[] = new byte[value.readableByteCount()];
                        value.read(data);
                        String s = new String(data);
                        LOGGER.info("Got data: {}", s);
                        body = s;
                        sink.success();
                    }
                });
            });
        }

        @Override
        protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            throw new RuntimeException("Not implemented");
        }

        @Override
        protected void applyStatusCode() {
            LOGGER.info("Write statusCode: {}", getStatusCode());
        }

        @Override
        protected void applyHeaders() {
            LOGGER.info("Write headers: {}", getHeaders());
        }

        @Override
        protected void applyCookies() {
            LOGGER.info("Write cookies: {}", getCookies());
        }

        public String getBody() {
            return body;
        }
    }
}
