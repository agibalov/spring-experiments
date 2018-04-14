package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RefreshTokenTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(RefreshTokenTest.class);

    private final static String USERNAME = "user1";
    private final static String PASSWORD = "user1password";
    private final static String CLIENT_ID = "MyClientId2";
    private final static String CLIENT_SECRET = "MyClientId2Secret";

    @Test
    public void canUseRefreshToken() throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new NoErrorsResponseErrorHandler());

        ResponseEntity<Map<String, String>> oAuthTokenResponseEntity = getAccessTokenByUsernameAndPassword(
                restTemplate, USERNAME, PASSWORD);
        assertEquals(HttpStatus.OK, oAuthTokenResponseEntity.getStatusCode());

        Map<String, String> oAuthTokenResponseBody = oAuthTokenResponseEntity.getBody();
        String accessToken = oAuthTokenResponseBody.get("access_token");
        int expiresInSeconds = Integer.valueOf(oAuthTokenResponseBody.get("expires_in"));
        String refreshToken = oAuthTokenResponseBody.get("refresh_token");

        {
            ResponseEntity<Map<String, String>> testResponseEntity = getTestResource(restTemplate, accessToken);
            assertEquals(
                    "PRINCIPAL is user 'user1' ([ROLE_USER]) [clientId=MyClientId2]",
                    testResponseEntity.getBody().get("message"));
        }

        Thread.sleep(1000 * expiresInSeconds + 1000);

        {
            ResponseEntity<Map<String, String>> testResponseEntity = getTestResource(restTemplate, accessToken);
            assertEquals(HttpStatus.UNAUTHORIZED, testResponseEntity.getStatusCode());
        }

        ResponseEntity<Map<String, String>> refreshResponseEntity = getAccessTokenByRefreshToken(restTemplate, refreshToken);
        assertEquals(HttpStatus.OK, refreshResponseEntity.getStatusCode());

        String newAccessToken = refreshResponseEntity.getBody().get("access_token");

        {
            ResponseEntity<Map<String, String>> testResponseEntity = getTestResource(restTemplate, newAccessToken);
            assertEquals(
                    "PRINCIPAL is user 'user1' ([ROLE_USER]) [clientId=MyClientId2]",
                    testResponseEntity.getBody().get("message"));
        }
    }

    private ResponseEntity<Map<String, String>> getAccessTokenByRefreshToken(
            RestTemplate restTemplate,
            String refreshToken) {

        MultiValueMap<String, String> refreshRequestBody = new LinkedMultiValueMap<>();
        refreshRequestBody.add("grant_type", "refresh_token");
        refreshRequestBody.add("refresh_token", refreshToken);

        MultiValueMap<String, String> refreshHeaders = new LinkedMultiValueMap<>();
        refreshHeaders.add(
                "Authorization",
                "Basic " + Base64Utils.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));

        RequestEntity refreshRequestEntity = new RequestEntity(
                refreshRequestBody,
                refreshHeaders,
                HttpMethod.POST,
                URI.create("http://localhost:8080/oauth/token"));
        ResponseEntity<Map<String, String>> refreshResponseEntity = restTemplate.exchange(
                refreshRequestEntity,
                new ParameterizedTypeReference<Map<String, String>>() {});
        LOGGER.info("refreshResponseEntity body: {}", refreshResponseEntity.getBody());
        return refreshResponseEntity;
    }

    private ResponseEntity<Map<String, String>> getAccessTokenByUsernameAndPassword(
            RestTemplate restTemplate,
            String username,
            String password) {

        MultiValueMap<String, String> oAuthTokenRequestBody = new LinkedMultiValueMap<>();
        oAuthTokenRequestBody.add("grant_type", "password");
        oAuthTokenRequestBody.add("username", username);
        oAuthTokenRequestBody.add("password", password);

        MultiValueMap<String, String> oAuthTokenRequestHeaders = new LinkedMultiValueMap<>();
        oAuthTokenRequestHeaders.add(
                "Authorization",
                "Basic " + Base64Utils.encodeToString((CLIENT_ID + ":" + CLIENT_SECRET).getBytes()));

        RequestEntity oAuthTokenRequestEntity = new RequestEntity(
                oAuthTokenRequestBody,
                oAuthTokenRequestHeaders,
                HttpMethod.POST,
                URI.create("http://localhost:8080/oauth/token"));
        return restTemplate.exchange(
                oAuthTokenRequestEntity,
                new ParameterizedTypeReference<Map<String, String>>() {});
    }

    private static ResponseEntity<Map<String, String>> getTestResource(RestTemplate restTemplate, String accessToken) {
        MultiValueMap<String, String> testRequestHeaders = new LinkedMultiValueMap<>();
        testRequestHeaders.add("Authorization", "Bearer " + accessToken);
        ResponseEntity<Map<String, String>> testResponseEntity = restTemplate.exchange(
                new RequestEntity(
                        testRequestHeaders,
                        HttpMethod.GET,
                        URI.create("http://localhost:8080/")),
                new ParameterizedTypeReference<Map<String, String>>() {
                });
        LOGGER.info("testResponseEntity body: {}", testResponseEntity.getBody());
        return testResponseEntity;
    }

    @Import(App.class)
    public static class Config {
    }

    private static class NoErrorsResponseErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return false;
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
        }
    }
}
