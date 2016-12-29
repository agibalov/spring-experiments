package me.loki2302.calc;

import me.loki2302.add.api.AddServiceAddNumbersRequestDto;
import me.loki2302.add.api.AddServiceAddNumbersResponseDto;
import me.loki2302.calc.api.CalcServiceAddNumbersRequestDto;
import me.loki2302.calc.api.CalcServiceAddNumbersResponseDto;
import me.loki2302.calc.api.CalcServiceSubNumbersRequestDto;
import me.loki2302.calc.api.CalcServiceSubNumbersResponseDto;
import me.loki2302.sub.api.SubServiceSubNumbersRequestDto;
import me.loki2302.sub.api.SubServiceSubNumbersResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
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

    @Component
    public static class DiscoveryReporter {
        private final Logger LOGGER = LoggerFactory.getLogger(DiscoveryReporter.class);

        @Autowired
        private DiscoveryClient discoveryClient;

        @Scheduled(fixedRate = 1000)
        public void reportServices() {
            List<String> services = discoveryClient.getServices();
            String serviceListString;
            if(services != null) {
                serviceListString = String.join(", ", services);
            } else {
                serviceListString = "null";
            }
            LOGGER.info("Services: {}", serviceListString);

            List<ServiceInstance> addServiceInstances = null;
            try {
                addServiceInstances = discoveryClient.getInstances("AddService");
            } catch (NullPointerException e) {
                // Sometimes it throws NPE. Is it a bug?
            }
            String addServiceInstanceListString;
            if(addServiceInstances != null) {
                addServiceInstanceListString = String.join(", ", addServiceInstances.stream()
                        .map(i -> String.format("{id=%s, uri=%s}", i.getServiceId(), i.getUri()))
                        .collect(Collectors.toList()));
            } else {
                addServiceInstanceListString = "null";
            }
            LOGGER.info("AddService instances: {}", addServiceInstanceListString);
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
