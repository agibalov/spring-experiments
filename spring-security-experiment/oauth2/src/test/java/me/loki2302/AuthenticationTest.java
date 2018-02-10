package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
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
public class AuthenticationTest {
    @Test
    public void canAuthenticateAsClient() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> oAuthTokenRequestBody = new LinkedMultiValueMap<>();
        oAuthTokenRequestBody.add("grant_type", "client_credentials");

        MultiValueMap<String, String> oAuthTokenRequestHeaders = new LinkedMultiValueMap<>();
        oAuthTokenRequestHeaders.add(
                "Authorization",
                "Basic " + Base64Utils.encodeToString("MyClientId1:MyClientId1Secret".getBytes()));

        ResponseEntity<Map<String, String>> oAuthTokenResponseEntity = restTemplate.exchange(
                new RequestEntity(
                        oAuthTokenRequestBody,
                        oAuthTokenRequestHeaders,
                        HttpMethod.POST,
                        URI.create("http://localhost:8080/oauth/token")),
                new ParameterizedTypeReference<Map<String, String>>() {});

        /*
        {
            access_token=7595e240-4a0f-4636-931d-f35af76831fd,
            token_type=bearer,
            expires_in=43199,
            scope=read
        }
         */

        Map<String, String> oAuthTokenResponseBody = oAuthTokenResponseEntity.getBody();
        assertNotNull(oAuthTokenResponseBody.get("access_token"));
        assertEquals("bearer", oAuthTokenResponseBody.get("token_type"));
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
        System.out.println(testResponseEntity.getBody());

        assertEquals("PRINCIPAL is client 'MyClientId1' ([ROLE_CLIENT])", testResponseEntity.getBody().get("message"));
    }

    @Test
    public void canAuthenticateAsUser() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> oAuthTokenRequestBody = new LinkedMultiValueMap<>();
        oAuthTokenRequestBody.add("grant_type", "password");
        oAuthTokenRequestBody.add("username", "user1");
        oAuthTokenRequestBody.add("password", "user1password");

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

        /*
        {
            access_token=70c133f4-8adf-451e-9c93-89285e785b07,
            token_type=bearer,
            refresh_token=90a4fab1-ee87-4648-8fb2-512d09232b94,
            expires_in=43199,
            scope=read
        }
         */

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
        System.out.println(testResponseEntity.getBody());

        assertEquals(
                "PRINCIPAL is user 'user1' ([ROLE_USER]) [clientId=MyClientId1]",
                testResponseEntity.getBody().get("message"));
    }

    @Test
    public void canMakeARequestWithoutAuthentication() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> testRequestHeaders = new LinkedMultiValueMap<>();
        ResponseEntity<Map<String, String>> testResponseEntity = restTemplate.exchange(
                new RequestEntity(
                        testRequestHeaders,
                        HttpMethod.GET,
                        URI.create("http://localhost:8080/")),
                new ParameterizedTypeReference<Map<String, String>>() {});
        System.out.println(testResponseEntity.getBody());

        assertEquals("PRINCIPAL is null", testResponseEntity.getBody().get("message"));
    }

    @Import(App.class)
    public static class Config {
    }
}
