package me.loki2302.app2;

import me.loki2302.app.App;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class App2 {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize("100000MB");
        factory.setMaxRequestSize("100000MB");
        return factory.createMultipartConfig();
    }

    @RestController
    public static class UploadController {
        public long uploadedFileSize;

        @RequestMapping(value = "/upload", method = RequestMethod.POST)
        public String handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
            uploadedFileSize = 0;

            InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream());
            try {
                while(inputStreamReader.read() != -1) {
                    ++uploadedFileSize;
                }
            } finally {
                inputStreamReader.close();
            }

            return "hi there";
        }
    }
}