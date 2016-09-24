package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@MyAppTest
@RunWith(SpringRunner.class)
public class DummyTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(DummyTest.class);

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void dummy() {
        TwoNumbersDto twoNumbersDto = new TwoNumbersDto();
        twoNumbersDto.a = 2;
        twoNumbersDto.b = 3;

        LOGGER.info("Going to make a request to /add");

        ResultDto resultDto = restTemplate.postForObject(
                "http://localhost:8080/add",
                twoNumbersDto,
                ResultDto.class);

        assertEquals(5, resultDto.result);
    }
}
