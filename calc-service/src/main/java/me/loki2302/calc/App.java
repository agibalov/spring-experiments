package me.loki2302.calc;

import me.loki2302.add.api.AddServiceAddNumbersRequestDto;
import me.loki2302.add.api.AddServiceAddNumbersResponseDto;
import me.loki2302.calc.api.CalcServiceAddNumbersRequestDto;
import me.loki2302.calc.api.CalcServiceAddNumbersResponseDto;
import me.loki2302.calc.api.CalcServiceSubNumbersRequestDto;
import me.loki2302.calc.api.CalcServiceSubNumbersResponseDto;
import me.loki2302.sub.api.SubServiceSubNumbersRequestDto;
import me.loki2302.sub.api.SubServiceSubNumbersResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class ApiController {
        @Autowired
        private RestTemplate restTemplate;

        @RequestMapping(value = "/api/add", method = RequestMethod.POST)
        public CalcServiceAddNumbersResponseDto addNumbers(
                @RequestBody CalcServiceAddNumbersRequestDto requestDto) {

            AddServiceAddNumbersRequestDto addServiceAddNumbersRequestDto =
                    new AddServiceAddNumbersRequestDto();
            addServiceAddNumbersRequestDto.a = requestDto.a;
            addServiceAddNumbersRequestDto.b = requestDto.b;

            AddServiceAddNumbersResponseDto addServiceAddNumbersResponseDto = restTemplate.postForObject(
                    "http://AddService/api/add",
                    addServiceAddNumbersRequestDto,
                    AddServiceAddNumbersResponseDto.class);

            CalcServiceAddNumbersResponseDto responseDto = new CalcServiceAddNumbersResponseDto();
            responseDto.result = addServiceAddNumbersResponseDto.result;

            return responseDto;
        }

        @RequestMapping(value = "/api/sub", method = RequestMethod.POST)
        public CalcServiceSubNumbersResponseDto addNumbers(
                @RequestBody CalcServiceSubNumbersRequestDto requestDto) {

            SubServiceSubNumbersRequestDto subServiceSubNumbersRequestDto =
                    new SubServiceSubNumbersRequestDto();
            subServiceSubNumbersRequestDto.a = requestDto.a;
            subServiceSubNumbersRequestDto.b = requestDto.b;

            SubServiceSubNumbersResponseDto addServiceAddNumbersResponseDto = restTemplate.postForObject(
                    "http://SubService/api/sub",
                    subServiceSubNumbersRequestDto,
                    SubServiceSubNumbersResponseDto.class);

            CalcServiceSubNumbersResponseDto responseDto = new CalcServiceSubNumbersResponseDto();
            responseDto.result = addServiceAddNumbersResponseDto.result;

            return responseDto;
        }
    }

    @Configuration
    public static class Config {
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}
