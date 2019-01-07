package me.loki2302;

import me.loki2302.add.api.AddServiceAddNumbersRequestDto;
import me.loki2302.add.api.AddServiceAddNumbersResponseDto;
import me.loki2302.calc.api.CalcServiceAddNumbersRequestDto;
import me.loki2302.calc.api.CalcServiceAddNumbersResponseDto;
import me.loki2302.calc.api.CalcServiceSubNumbersRequestDto;
import me.loki2302.calc.api.CalcServiceSubNumbersResponseDto;
import me.loki2302.sub.api.SubServiceSubNumbersRequestDto;
import me.loki2302.sub.api.SubServiceSubNumbersResponseDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class DummyTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyTest.class);

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void canAddNumbersUsingAddService() {
        ping("http://172.25.0.11:8080/info");

        AddServiceAddNumbersRequestDto requestDto = new AddServiceAddNumbersRequestDto();
        requestDto.a = 2;
        requestDto.b = 3;
        AddServiceAddNumbersResponseDto responseDto = restTemplate.postForObject(
                "http://172.25.0.11:8080/api/add",
                requestDto,
                AddServiceAddNumbersResponseDto.class);
        assertEquals(5, responseDto.result);
    }

    @Test
    public void canSubtractNumbersUsingSubService() {
        ping("http://172.25.0.22:8080/info");

        SubServiceSubNumbersRequestDto requestDto = new SubServiceSubNumbersRequestDto();
        requestDto.a = 2;
        requestDto.b = 3;
        SubServiceSubNumbersResponseDto responseDto = restTemplate.postForObject(
                "http://172.25.0.22:8080/api/sub",
                requestDto,
                SubServiceSubNumbersResponseDto.class);
        assertEquals(-1, responseDto.result);
    }

    @Test
    public void canAddNumbersUsingCalcService() {
        ping("http://172.25.0.11:8080/info");
        ping("http://172.25.0.12:8080/info");
        ping("http://172.25.0.13:8080/info");
        ping("http://172.25.0.22:8080/info");
        ping("http://172.25.0.33:8080/info");

        CalcServiceAddNumbersRequestDto requestDto = new CalcServiceAddNumbersRequestDto();
        requestDto.a = 2;
        requestDto.b = 3;
        CalcServiceAddNumbersResponseDto responseDto = restTemplate.postForObject(
                "http://172.25.0.33:8080/api/add",
                requestDto,
                CalcServiceAddNumbersResponseDto.class);
        assertEquals(5, responseDto.result);
    }

    @Test
    public void canSubtractNumbersUsingCalcService() {
        ping("http://172.25.0.11:8080/info");
        ping("http://172.25.0.12:8080/info");
        ping("http://172.25.0.13:8080/info");
        ping("http://172.25.0.22:8080/info");
        ping("http://172.25.0.33:8080/info");

        CalcServiceSubNumbersRequestDto requestDto = new CalcServiceSubNumbersRequestDto();
        requestDto.a = 2;
        requestDto.b = 3;
        CalcServiceSubNumbersResponseDto responseDto = restTemplate.postForObject(
                "http://172.25.0.33:8080/api/sub",
                requestDto,
                CalcServiceSubNumbersResponseDto.class);
        assertEquals(-1, responseDto.result);
    }

    private static void ping(String url) {
        RestTemplate restTemplate = new RestTemplate();

        for(int i = 1; i <= 100; ++i) {
            ResponseEntity<String> responseEntity = null;
            ResourceAccessException resourceAccessException = null;
            try {
                responseEntity = restTemplate.getForEntity(url, String.class);
            } catch (ResourceAccessException e) {
                resourceAccessException = e;
            }

            if(resourceAccessException != null) {
                LOGGER.info("Pinging {} - attempt #{} failed ({})", url, i, resourceAccessException.getMessage());
            } else if(!responseEntity.getStatusCode().is2xxSuccessful()) {
                LOGGER.info("Pinging {} - attempt #{} failed ({})", url, i, responseEntity.getStatusCode());
            } else {
                LOGGER.info("Pinging {} - attempt #{} succeeded", url, i);
                break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {}
        }
    }

    @SpringBootApplication
    @EnableDiscoveryClient
    public static class Config {
        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
