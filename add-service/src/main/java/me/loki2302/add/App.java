package me.loki2302.add;

import me.loki2302.add.api.AddServiceAddNumbersRequestDto;
import me.loki2302.add.api.AddServiceAddNumbersResponseDto;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class ApiController {
        @RequestMapping(value = "/api/add", method = RequestMethod.POST)
        public AddServiceAddNumbersResponseDto addNumbers(
                @RequestBody AddServiceAddNumbersRequestDto requestDto) {

            AddServiceAddNumbersResponseDto responseDto = new AddServiceAddNumbersResponseDto();
            responseDto.result = requestDto.a + requestDto.b;
            return responseDto;
        }
    }
}
