package me.loki2302.servlet;

import org.hibernate.validator.constraints.Range;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ResponseEntityExceptionHandlerTest {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void tryThrowRuntimeException() {
        String url = MvcUriComponentsBuilder.relativeTo(UriComponentsBuilder.fromHttpUrl("http://localhost:8080"))
                .withMethodCall(MvcUriComponentsBuilder.on(DummyController.class).throwRuntimeException())
                .toUriString();

        ResponseEntity<ErrorDto> responseEntity = restTemplate.getForEntity(url, ErrorDto.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assert.assertEquals("throwable handler", responseEntity.getBody().message);
    }

    @Test
    public void tryThrowSomethingNotFoundApiException() {
        String url = MvcUriComponentsBuilder.relativeTo(UriComponentsBuilder.fromHttpUrl("http://localhost:8080"))
                .withMethodCall(MvcUriComponentsBuilder.on(DummyController.class).throwSomethingNotFoundApiException())
                .toUriString();

        ResponseEntity<ErrorDto> responseEntity = restTemplate.getForEntity(url, ErrorDto.class);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Assert.assertEquals("something not found", responseEntity.getBody().message);
    }

    @Test
    public void tryPostRequestWhenResourceOnlySupportsGet() {
        String url = MvcUriComponentsBuilder.relativeTo(UriComponentsBuilder.fromHttpUrl("http://localhost:8080"))
                .withMethodCall(MvcUriComponentsBuilder.on(DummyController.class).handleGet())
                .toUriString();

        ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(url, null, ErrorDto.class);
        Assert.assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        Assert.assertEquals("handleHttpRequestMethodNotSupported", responseEntity.getBody().message);
    }

    @Test
    public void tryHandleMissingPathVariable() {
        String url = "http://localhost:8080/exampleForHandleMissingPathVariable/whatever";

        ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(url, null, ErrorDto.class);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        Assert.assertEquals("handleMissingPathVariable", responseEntity.getBody().message);
    }

    @Test
    public void tryHandleTypeMismatch() {
        String url = "http://localhost:8080/exampleForHandleTypeMismatch/whatever";

        ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(url, null, ErrorDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("handleTypeMismatch", responseEntity.getBody().message);
    }

    @Test
    public void tryHandleMethodArgumentNotValid() {
        String url = "http://localhost:8080/exampleForHandleMethodArgumentNotValid";

        SomeRequestDto someRequestDto = new SomeRequestDto();
        someRequestDto.something = 11;

        ResponseEntity<ErrorDto> responseEntity = restTemplate.postForEntity(url, someRequestDto, ErrorDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("handleMethodArgumentNotValid", responseEntity.getBody().message);
    }

    @Test
    public void tryHandleHttpMessageNotReadable_notJson() {
        String url = "http://localhost:8080/exampleForHandleHttpMessageNotReadable";

        RequestEntity requestEntity = RequestEntity.post(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body("it is not JSON");

        ResponseEntity<ErrorDto> responseEntity = restTemplate.exchange(requestEntity, ErrorDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("handleHttpMessageNotReadable", responseEntity.getBody().message);
    }

    @Test
    public void tryHandleHttpMessageNotReadable_wrongSchemaJson() {
        String url = "http://localhost:8080/exampleForHandleHttpMessageNotReadable";

        RequestEntity requestEntity = RequestEntity.post(URI.create(url))
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"something\": \"x\"}");

        ResponseEntity<ErrorDto> responseEntity = restTemplate.exchange(requestEntity, ErrorDto.class);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        Assert.assertEquals("handleHttpMessageNotReadable", responseEntity.getBody().message);
    }

    @Test
    public void tryHandleHttpMediaTypeNotSupported() {
        String url = "http://localhost:8080/exampleForHandleHttpMessageNotReadable";

        RequestEntity requestEntity = RequestEntity.post(URI.create(url))
                .contentType(MediaType.APPLICATION_XML)
                .body("<xml>1</xml>");

        ResponseEntity<ErrorDto> responseEntity = restTemplate.exchange(requestEntity, ErrorDto.class);
        Assert.assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());
        Assert.assertEquals("handleHttpMediaTypeNotSupported", responseEntity.getBody().message);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }

        @Bean
        public ErrorHandlingControllerAdvice errorHandlingControllerAdvice() {
            return new ErrorHandlingControllerAdvice();
        }

        @Bean
        public RestTemplate restTemplate() {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new SilentResponseErrorHandler());
            return restTemplate;
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping("/throwRuntimeException")
        public ResponseEntity<?> throwRuntimeException() {
            throw new RuntimeException("hello");
        }

        @RequestMapping("/throwSomethingNotFoundApiException")
        public ResponseEntity<?> throwSomethingNotFoundApiException() {
            throw new SomethingNotFoundApiException();
        }

        @RequestMapping(value = "/handleGet", method = RequestMethod.GET)
        public ResponseEntity<String> handleGet() {
            return ResponseEntity.ok("I am handleGet");
        }

        @RequestMapping("/exampleForHandleMissingPathVariable/{x}")
        public ResponseEntity<String> exampleForHandleMissingPathVariable(@PathVariable("y") String y) {
            return ResponseEntity.ok("I am exampleForHandleMissingPathVariable");
        }

        @RequestMapping("/exampleForHandleTypeMismatch/{x}")
        public ResponseEntity<String> exampleForHandleTypeMismatch(@PathVariable("x") int x) {
            return ResponseEntity.ok("I am exampleForHandleTypeMismatch");
        }

        @RequestMapping("/exampleForHandleMethodArgumentNotValid")
        public ResponseEntity<String> exampleForHandleMethodArgumentNotValid(@RequestBody @Valid SomeRequestDto someRequestDto) {
            return ResponseEntity.ok("I am exampleForHandleMethodArgumentNotValid");
        }

        @RequestMapping("/exampleForHandleHttpMessageNotReadable")
        public ResponseEntity<String> exampleForHandleHttpMessageNotReadable(@RequestBody SomeRequestDto someRequestDto) {
            return ResponseEntity.ok("I am exampleForHandleHttpMessageNotReadable");
        }
    }

    /**
     * All exception handlers MUST be within a single @ControllerAdvice class to make
     * precedence consistent - more concrete types on the top, less concrete types on the bottom.
     * Otherwise it may end up in using handler for Throwable when in another class there's a more
     * concrete handler.
     */
    @ControllerAdvice
    public static class ErrorHandlingControllerAdvice extends ResponseEntityExceptionHandler {
        private final static Logger LOGGER = LoggerFactory.getLogger(ErrorHandlingControllerAdvice.class);

        @Override
        protected ResponseEntity<Object> handleExceptionInternal(
                Exception ex,
                Object body,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleExceptionInternal");
            return super.handleExceptionInternal(ex, body, headers, status, request);
        }

        @Nullable
        @Override
        protected ResponseEntity<Object> handleAsyncRequestTimeoutException(
                AsyncRequestTimeoutException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest webRequest) {

            // ?

            LOGGER.info("handleAsyncRequestTimeoutException");
            return new ResponseEntity<>(new ErrorDto("handleAsyncRequestTimeoutException"), HttpStatus.SERVICE_UNAVAILABLE);
        }

        @Override
        protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
                HttpRequestMethodNotSupportedException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // @RequestMapping describes that mapping only exists for GET, but user sends POST

            LOGGER.info("handleHttpRequestMethodNotSupported");
            return new ResponseEntity<>(new ErrorDto("handleHttpRequestMethodNotSupported"), HttpStatus.METHOD_NOT_ALLOWED);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
                HttpMediaTypeNotSupportedException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // We can only accept application/json, but they give us application/xml

            LOGGER.info("handleHttpMediaTypeNotSupported");
            return new ResponseEntity<>(new ErrorDto("handleHttpMediaTypeNotSupported"), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
                HttpMediaTypeNotAcceptableException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleHttpMediaTypeNotAcceptable");
            return super.handleHttpMediaTypeNotAcceptable(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleMissingPathVariable(
                MissingPathVariableException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // @PathVariable("x") refers to x, but there is no x in @RequestMapping()

            LOGGER.info("handleMissingPathVariable");
            return new ResponseEntity<>(new ErrorDto("handleMissingPathVariable"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @Override
        protected ResponseEntity<Object> handleMissingServletRequestParameter(
                MissingServletRequestParameterException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleMissingServletRequestParameter");
            return super.handleMissingServletRequestParameter(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleServletRequestBindingException(
                ServletRequestBindingException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleServletRequestBindingException");
            return super.handleServletRequestBindingException(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleConversionNotSupported(
                ConversionNotSupportedException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleConversionNotSupported");
            return super.handleConversionNotSupported(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleTypeMismatch(
                TypeMismatchException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // @PathVariable("x") requires x to be an integer, but user specifies non-numeric value

            LOGGER.info("handleTypeMismatch");
            return new ResponseEntity<>(new ErrorDto("handleTypeMismatch"), HttpStatus.BAD_REQUEST);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotReadable(
                HttpMessageNotReadableException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // We can accept application/json and user says their Content-type is application/json,
            // but the request body they send does not look like JSON

            // We can accept application/json and user says their Content-type is application/json,
            // and the request body is JSON, but its schema does not match what we expect

            LOGGER.info("handleHttpMessageNotReadable");
            return new ResponseEntity(new ErrorDto("handleHttpMessageNotReadable"), HttpStatus.BAD_REQUEST);
        }

        @Override
        protected ResponseEntity<Object> handleHttpMessageNotWritable(
                HttpMessageNotWritableException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleHttpMessageNotWritable");
            return super.handleHttpMessageNotWritable(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                MethodArgumentNotValidException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // Handles @RequestBody @Valid validation errors

            LOGGER.info("handleMethodArgumentNotValid");
            return new ResponseEntity<>(new ErrorDto("handleMethodArgumentNotValid"), HttpStatus.BAD_REQUEST);
        }

        @Override
        protected ResponseEntity<Object> handleMissingServletRequestPart(
                MissingServletRequestPartException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleMissingServletRequestPart");
            return super.handleMissingServletRequestPart(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleBindException(
                BindException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleBindException");
            return super.handleBindException(ex, headers, status, request);
        }

        @Override
        protected ResponseEntity<Object> handleNoHandlerFoundException(
                NoHandlerFoundException ex,
                HttpHeaders headers,
                HttpStatus status,
                WebRequest request) {

            // ?

            LOGGER.info("handleNoHandlerFoundException");
            return super.handleNoHandlerFoundException(ex, headers, status, request);
        }

        @ExceptionHandler(ApiException.class)
        public ResponseEntity<?> handleApiException(ApiException apiException) {
            LOGGER.info("handleApiException");
            return apiException.makeResponseEntity();
        }

        @ExceptionHandler(Throwable.class)
        public ResponseEntity<?> handleThrowable(Throwable throwable) {
            LOGGER.info("handleThrowable");
            return new ResponseEntity<>(new ErrorDto("throwable handler"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static class ErrorDto {
        public String message;

        public ErrorDto() {
        }

        public ErrorDto(String message) {
            this.message = message;
        }
    }

    public static class SomeRequestDto {
        @Range(min = 0, max = 10)
        public int something;
    }

    public static abstract class ApiException extends RuntimeException {
        public abstract ResponseEntity<?> makeResponseEntity();
    }

    public static class SomethingNotFoundApiException extends ApiException {
        @Override
        public ResponseEntity<?> makeResponseEntity() {
            return new ResponseEntity<>(new ErrorDto("something not found"), HttpStatus.NOT_FOUND);
        }
    }

    public static class SilentResponseErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
        }
    }
}
