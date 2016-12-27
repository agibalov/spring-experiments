package me.loki2302.sub;

import me.loki2302.sub.api.SubServiceSubNumbersRequestDto;
import me.loki2302.sub.api.SubServiceSubNumbersResponseDto;
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
        @RequestMapping(value = "/api/sub", method = RequestMethod.POST)
        public SubServiceSubNumbersResponseDto addNumbers(
                @RequestBody SubServiceSubNumbersRequestDto requestDto) {

            SubServiceSubNumbersResponseDto responseDto = new SubServiceSubNumbersResponseDto();
            responseDto.result = requestDto.a - requestDto.b;
            return responseDto;
        }
    }
}
