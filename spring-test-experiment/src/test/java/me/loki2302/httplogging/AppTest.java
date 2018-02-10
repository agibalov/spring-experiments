package me.loki2302.httplogging;

import me.loki2302.httplogging.app.ResultDto;
import me.loki2302.httplogging.app.TwoNumbersDto;
import me.loki2302.httplogging.logging.AppTestWithHttpLogging;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

@AppTestWithHttpLogging
@RunWith(SpringRunner.class)
public class AppTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppTest.class);

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void canAddTwoNumbers() {
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
