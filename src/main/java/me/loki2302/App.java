package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class DummyController {
        @RequestMapping(value = "/add", method = RequestMethod.POST)
        public ResultDto addNumbers(@RequestBody TwoNumbersDto twoNumbersDto) {
            ResultDto resultDto = new ResultDto();
            resultDto.a = twoNumbersDto.a;
            resultDto.b = twoNumbersDto.b;
            resultDto.result = twoNumbersDto.a + twoNumbersDto.b;
            return resultDto;
        }
    }
}
