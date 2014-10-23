package me.loki2302;

import com.mangofactory.swagger.plugin.EnableSwagger;
import com.wordnik.swagger.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);
    }

    @Configuration
    @EnableSwagger
    @ComponentScan
    @EnableAutoConfiguration
    public static class Config {
    }

    @Controller
    @Api(value = "Person controller", description = "CRUD API for people")
    public static class PersonController {
        @RequestMapping(value = "/", method = RequestMethod.POST)
        @ApiOperation(value = "Create a new person", notes = "Some notes about creating a new person")
        @ApiResponses({
                @ApiResponse(code = 201, message = "Message for 'created'", response = CreatedPersonDTO.class),
                @ApiResponse(code = 400, message = "Message for 'bad request'", response = BadRequestDTO.class)
        })
        public ResponseEntity<Object> createPerson(@RequestBody CreatePersonDTO createPersonDTO) {
            return new ResponseEntity<Object>("hello there", HttpStatus.CREATED);
        }
    }

    @ApiModel("Create Person DTO")
    public static class CreatePersonDTO {
        @ApiModelProperty(value = "person name", required = true)
        public String name;

        @ApiModelProperty(value = "person age", required = true)
        public int age;
    }

    @ApiModel("Created Person DTO")
    public static class CreatedPersonDTO {
        @ApiModelProperty(value = "person id", required = true)
        public int id;

        @ApiModelProperty(value = "person name", required = true)
        public String name;

        @ApiModelProperty(value = "person age", required = true)
        public int age;
    }

    @ApiModel("Bad request DTO")
    public static class BadRequestDTO {
        public int dummyBadRequestField;
    }
}
