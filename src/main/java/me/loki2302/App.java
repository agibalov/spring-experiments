package me.loki2302;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    @RestController
    public static class ApiController {
        @RequestMapping(value = "/api/notes", method = RequestMethod.POST)
        public NoteDto createNote(@RequestBody CreateNoteRequestDto createNoteRequestDto) {
            NoteDto noteDto = new NoteDto();
            noteDto.id = UUID.randomUUID().toString();
            noteDto.title = createNoteRequestDto.title;
            noteDto.description = createNoteRequestDto.description;
            return noteDto;
        }
    }

    public static class CreateNoteRequestDto {
        public String title;
        public String description;
    }

    public static class NoteDto {
        public String id;
        public String title;
        public String description;
    }
}
