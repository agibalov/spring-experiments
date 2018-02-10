package me.loki2302;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class ApiController {
        @RequestMapping(value = "/api/notes", method = RequestMethod.POST)
        public NoteDto createNote(@Valid @RequestBody CreateNoteRequestDto createNoteRequestDto) {
            NoteDto noteDto = new NoteDto();
            noteDto.id = UUID.randomUUID().toString();
            noteDto.title = createNoteRequestDto.title;
            noteDto.description = createNoteRequestDto.description;
            return noteDto;
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity handlerArgumentNotValidException(MethodArgumentNotValidException e) {
            BindingResult bindingResult = e.getBindingResult();
            Map<String, String> errorFields = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));

            ValidationErrorDto errorDto = new ValidationErrorDto();
            errorDto.error = "VALIDATION";
            errorDto.errorFields = errorFields;
            return new ResponseEntity(errorDto, HttpStatus.BAD_REQUEST);
        }
    }

    public static class CreateNoteRequestDto {
        @NotBlank
        public String title;

        @NotBlank
        public String description;
    }

    public static class NoteDto {
        public String id;
        public String title;
        public String description;
    }

    public static class ErrorDto {
        public String error;
    }

    public static class ValidationErrorDto extends ErrorDto {
        public Map<String, String> errorFields;
    }
}
