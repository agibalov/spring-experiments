package io.agibalov;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth.consumer.client.CoreOAuthConsumerSupport;
import org.springframework.security.oauth.consumer.client.OAuthRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AppTest {
    @Test
    public void canAccessWithGoodCredentials() {
        RestTemplate restTemplate = restTemplate("aaa123", "thesecret");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/api/hello", String.class);
        log.info("Response: {}", responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("hello world aaa123", responseEntity.getBody());
    }

    @Test
    public void cantAccessWithIncorrectSecret() {
        RestTemplate restTemplate = restTemplate("aaa123", "badsecret");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/api/hello", String.class);
        log.info("Response: {}", responseEntity);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    public void cantAccessWithUnknownKey() {
        RestTemplate restTemplate = restTemplate("bbb234", "thesecret");
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8080/api/hello", String.class);
        log.info("Response: {}", responseEntity);
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    private static RestTemplate restTemplate(String consumerKey, String consumerSecret) {
        OAuthRestTemplate oAuthRestTemplate = new OAuthRestTemplate(
                new OAuthConsumerKeyAndSecret(consumerKey, consumerSecret));

        CoreOAuthConsumerSupport coreOAuthConsumerSupport = new CoreOAuthConsumerSupport();
        coreOAuthConsumerSupport.setSignatureFactory(new HmacSha256OAuthSignatureMethodFactory());

        oAuthRestTemplate.setSupport(coreOAuthConsumerSupport);

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .build();
        oAuthRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        oAuthRestTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });

        return oAuthRestTemplate;
    }
}
