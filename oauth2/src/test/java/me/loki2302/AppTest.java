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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AppTest {
    @Test
    public void canAuthenticateAsClient() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "client_credentials");

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + Base64Utils.encodeToString("MyClientId1:MyClientId1Secret".getBytes()));

        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                new RequestEntity(
                        requestBody,
                        headers,
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

        Map<String, String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody.get("access_token"));
        assertEquals("bearer", responseBody.get("token_type"));
        assertNotNull(responseBody.get("expires_in"));
        assertEquals("read", responseBody.get("scope"));

        MultiValueMap<String, String> headers2 = new LinkedMultiValueMap<>();
        headers2.add("Authorization", "Bearer " + responseBody.get("access_token"));
        ResponseEntity<Map<String, String>> responseEntity2 = restTemplate.exchange(
                new RequestEntity(
                        headers2,
                        HttpMethod.GET,
                        URI.create("http://localhost:8080/")),
                new ParameterizedTypeReference<Map<String, String>>() {});
        System.out.println(responseEntity2.getBody());

        assertEquals("PRINCIPAL is client 'MyClientId1' ([ROLE_CLIENT])", responseEntity2.getBody().get("message"));
    }

    @Test
    public void canAuthenticateAsUser() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", "password");
        requestBody.add("username", "user1");
        requestBody.add("password", "user1password");

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Basic " + Base64Utils.encodeToString("MyClientId1:MyClientId1Secret".getBytes()));

        RequestEntity requestEntity = new RequestEntity(
                requestBody,
                headers,
                HttpMethod.POST,
                URI.create("http://localhost:8080/oauth/token"));
        ResponseEntity<Map<String, String>> responseEntity = restTemplate.exchange(
                requestEntity,
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

        Map<String, String> responseBody = responseEntity.getBody();
        assertNotNull(responseBody.get("access_token"));
        assertEquals("bearer", responseBody.get("token_type"));
        assertNotNull(responseBody.get("refresh_token"));
        assertNotNull(responseBody.get("expires_in"));
        assertEquals("read", responseBody.get("scope"));

        //
        MultiValueMap<String, String> headers2 = new LinkedMultiValueMap<>();
        headers2.add("Authorization", "Bearer " + responseBody.get("access_token"));
        ResponseEntity<Map<String, String>> responseEntity2 = restTemplate.exchange(
                new RequestEntity(
                        headers2,
                        HttpMethod.GET,
                        URI.create("http://localhost:8080/")),
                new ParameterizedTypeReference<Map<String, String>>() {});
        System.out.println(responseEntity2.getBody());

        assertEquals("PRINCIPAL is user 'user1' ([ROLE_USER])", responseEntity2.getBody().get("message"));
    }

    @Test
    public void canMakeARequestWithoutAuthentication() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, String> headers2 = new LinkedMultiValueMap<>();
        ResponseEntity<Map<String, String>> responseEntity2 = restTemplate.exchange(
                new RequestEntity(
                        headers2,
                        HttpMethod.GET,
                        URI.create("http://localhost:8080/")),
                new ParameterizedTypeReference<Map<String, String>>() {});
        System.out.println(responseEntity2.getBody());

        assertEquals("PRINCIPAL is null", responseEntity2.getBody().get("message"));
    }

    @Import(App.class)
    public static class Config {
    }
}
