package me.loki2302;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@WebAppConfiguration
@SpringApplicationConfiguration(classes = App.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class AppTest {
    @Test
    public void forwardRuleShouldWork() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new NoopRedirectStrategy())
                .build();
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/forward-me", String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("hello there", responseEntity.getBody());
    }

    @Test
    public void redirectRuleShouldWork() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new NoopRedirectStrategy())
                .build();
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(httpComponentsClientHttpRequestFactory);
        ResponseEntity responseEntity = restTemplate.getForEntity(
                "http://localhost:8080/redirect-me", Void.class);
        assertEquals(HttpStatus.FOUND, responseEntity.getStatusCode());
        assertEquals("http://localhost:8080/", responseEntity.getHeaders().getLocation().toString());
    }

    private static class NoopRedirectStrategy implements RedirectStrategy {
        @Override
        public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            return false;
        }

        @Override
        public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
            return null;
        }
    }
}
