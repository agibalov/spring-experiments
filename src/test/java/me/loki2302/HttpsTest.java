package me.loki2302;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import static org.junit.Assert.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = {
        "server.port=8443",
        "server.ssl.key-store=classpath:keystore.jks",
        "server.ssl.key-store-password=mykeystorepassword",
        "server.ssl.key-password=mykeypassword"})
@RunWith(SpringRunner.class)
public class HttpsTest {
    @Test
    public void requestShouldFailWhenUsingDefaultClientConfiguration() {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(closeableHttpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        try {
            restTemplate.getForObject("https://localhost:8443/", String.class);
            fail();
        } catch (ResourceAccessException e) {
            assertTrue(e.getCause() instanceof SSLHandshakeException);
        }
    }

    @Test
    public void requestShouldSucceedWhenIgnoringSslErrors() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(closeableHttpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String result = restTemplate.getForObject("https://localhost:8443/", String.class);
        assertEquals("hello there", result);
    }

    @Test
    public void requestShouldSucceedWhenUsingKeystoreJks() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, CertificateException, IOException {
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(getClass().getResource("/keystore.jks"), "mykeystorepassword".toCharArray(), TrustSelfSignedStrategy.INSTANCE)
                .build();
        SSLConnectionSocketFactory sslConnectionSocketFactory =
                new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

        CloseableHttpClient closeableHttpClient = HttpClients.custom()
                .setSSLSocketFactory(sslConnectionSocketFactory)
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(closeableHttpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        String result = restTemplate.getForObject("https://localhost:8443/", String.class);
        assertEquals("hello there", result);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public DummyController dummyController() {
            return new DummyController();
        }
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String index() {
            return "hello there";
        }
    }
}
