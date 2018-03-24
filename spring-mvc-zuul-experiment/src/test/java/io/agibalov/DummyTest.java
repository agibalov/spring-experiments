package io.agibalov;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureWireMock(port = 8888)
public class DummyTest {
    @Test
    public void dummy() {
        stubFor(get(urlEqualTo("/something"))
                .willReturn(aResponse().withStatus(200).withBody("hi there")));

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject("http://localhost:8080/api/something", String.class);
        assertEquals("hi there", result);

        verify(getRequestedFor(urlEqualTo("/something")));
    }

    @SpringBootApplication
    @EnableZuulProxy
    public static class Config {
    }
}
