package me.loki2302;

import com.mangofactory.swagger.plugin.EnableSwagger;
import com.wordnik.swagger.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RestController
    @Api(value = "Person controller", description = "CRUD API for people")
    public static class PersonController {
        @RequestMapping(value = "/", method = RequestMethod.POST)
        @ApiOperation(value = "Create a new person", notes = "Some notes about creating a new person")
        @ApiResponses({
                // WTF: response classes are ignored: only code and message are used
                @ApiResponse(code = 201, message = "Message for 'created'", response = PersonDTO.class),
                @ApiResponse(code = 400, message = "Message for 'bad request'", response = BadRequestDTO.class)

                // WTF: extra statuses like 401 and 403 are added automatically. WHY???
        })
        // WTF: there's no special handling for ResponseEntity<T>, it just gets described as is
        // WTF: can't play with it using Swagger UI
        public ResponseEntity<Object> createPerson(@RequestBody CreatePersonDTO createPersonDTO) {
            return new ResponseEntity<Object>("hello there", HttpStatus.CREATED);
        }

        @RequestMapping(value = "/{id}", method = RequestMethod.GET)
        @ApiOperation(value = "Get an existing person", notes = "Use this to retrieve an existing person")
        @ApiResponses({
                @ApiResponse(code = 200, message = "Person found and here it is"),
                @ApiResponse(code = 404, message = "There's no such person")
        })
        public PersonDTO getPerson(
                @ApiParam(value = "Person identifier", required = true) @PathVariable("id") int personId) {

            PersonDTO personDTO = new PersonDTO();
            personDTO.id = personId;
            personDTO.age = 30;
            personDTO.name = "loki2302";
            return personDTO;
        }
    }

    // WTF: @ApiModel's value doesn't seem to be used anywhere
    @ApiModel("Create Person DTO")
    public static class CreatePersonDTO {
        @ApiModelProperty(value = "person name", required = true)
        public String name;

        @ApiModelProperty(value = "person age", required = true)
        public int age;
    }

    // WTF: @ApiModel's value doesn't seem to be used anywhere
    @ApiModel("Person DTO")
    public static class PersonDTO {
        @ApiModelProperty(value = "A unique person identifier", required = true)
        public int id;

        @ApiModelProperty(value = "Person name", required = true)
        public String name;

        @ApiModelProperty(value = "Person age", required = true)
        public int age;
    }

    // WTF: @ApiModel's value doesn't seem to be used anywhere
    @ApiModel("Bad request DTO")
    public static class BadRequestDTO {
        public int dummyBadRequestField;
    }
}
