package me.loki2302;

import io.swagger.annotations.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.ant;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);
    }

    @Configuration
    @EnableSwagger2
    @ComponentScan
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public Docket api() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .useDefaultResponseMessages(false)
                    .select()
                    .paths(or(ant("/api/*"), ant("/api/**/*")))
                    .build();
        }

        @Bean
        public ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("My API title")
                    .contact(new Contact(
                            "loki2302",
                            "http://loki2302.me",
                            "loki2302@loki2302.me"))
                    .description("My API description")
                    .license("My API license")
                    .licenseUrl("http://retask.me/license")
                    .termsOfServiceUrl("http://retask.me/tos")
                    .version("My API version")
                    .build();
        }
    }

    @RestController
    @Api(value = "Person controller", description = "CRUD API for people")
    public static class PersonController {
        @RequestMapping(value = "/api/", method = RequestMethod.POST)
        @ApiOperation(value = "Create a new person", notes = "Some notes about creating a new person")
        @ApiResponses({
                @ApiResponse(code = 201, message = "Message for 'created'", response = PersonDTO.class),
                @ApiResponse(code = 400, message = "Message for 'bad request'", response = BadRequestDTO.class)
        })
        public ResponseEntity<Object> createPerson(@RequestBody CreatePersonDTO createPersonDTO) {
            return new ResponseEntity<Object>("hello there", HttpStatus.CREATED);
        }

        @RequestMapping(value = "/api/{id}", method = RequestMethod.GET)
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

    @ApiModel("Create Person DTO")
    public static class CreatePersonDTO {
        @ApiModelProperty(value = "person name", required = true)
        public String name;

        @ApiModelProperty(value = "person age", required = true)
        public int age;
    }

    @ApiModel("Person DTO")
    public static class PersonDTO {
        @ApiModelProperty(value = "A unique person identifier", required = true)
        public int id;

        @ApiModelProperty(value = "Person name", required = true)
        public String name;

        @ApiModelProperty(value = "Person age", required = true)
        public int age;
    }

    @ApiModel("Bad request DTO")
    public static class BadRequestDTO {
        public int dummyBadRequestField;
    }
}
