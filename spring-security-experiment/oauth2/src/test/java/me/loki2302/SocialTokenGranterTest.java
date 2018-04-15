package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SocialTokenGranterTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(SocialTokenGranterTest.class);

    @Test
    public void canAuthenticateUsingSocialGrantType() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> oAuthTokenRequestBody = new LinkedMultiValueMap<>();
        oAuthTokenRequestBody.add("grant_type", "social");
        oAuthTokenRequestBody.add("provider", "google");
        oAuthTokenRequestBody.add("token", App.SocialTokenGranter.USER1_TOKEN);

        MultiValueMap<String, String> oAuthTokenRequestHeaders = new LinkedMultiValueMap<>();
        oAuthTokenRequestHeaders.add(
                "Authorization",
                "Basic " + Base64Utils.encodeToString("MyClientId1:MyClientId1Secret".getBytes()));

        RequestEntity oAuthTokenRequestEntity = new RequestEntity(
                oAuthTokenRequestBody,
                oAuthTokenRequestHeaders,
                HttpMethod.POST,
                URI.create("http://localhost:8080/oauth/token"));
        ResponseEntity<Map<String, String>> oAuthTokenResponseEntity = restTemplate.exchange(
                oAuthTokenRequestEntity,
                new ParameterizedTypeReference<Map<String, String>>() {});

        Map<String, String> oAuthTokenResponseBody = oAuthTokenResponseEntity.getBody();
        assertNotNull(oAuthTokenResponseBody.get("access_token"));
        assertEquals("bearer", oAuthTokenResponseBody.get("token_type"));
        assertNotNull(oAuthTokenResponseBody.get("refresh_token"));
        assertNotNull(oAuthTokenResponseBody.get("expires_in"));
        assertTrue(oAuthTokenResponseBody.get("scope").contains("read"));
        assertTrue(oAuthTokenResponseBody.get("scope").contains("cats"));
        assertTrue(oAuthTokenResponseBody.get("scope").contains("beer"));


        MultiValueMap<String, String> testRequestHeaders = new LinkedMultiValueMap<>();
        testRequestHeaders.add("Authorization", "Bearer " + oAuthTokenResponseBody.get("access_token"));
        ResponseEntity<Map<String, String>> testResponseEntity = restTemplate.exchange(
                new RequestEntity(
                        testRequestHeaders,
                        HttpMethod.GET,
                        URI.create("http://localhost:8080/")),
                new ParameterizedTypeReference<Map<String, String>>() {});
        LOGGER.info("testResponseEntity body: {}", testResponseEntity.getBody());

        assertEquals(
                "PRINCIPAL is user 'user1' ([ROLE_USER]) [clientId=MyClientId1]",
                testResponseEntity.getBody().get("message"));
    }

    @Import(App.class)
    public static class Config {
    }
}
