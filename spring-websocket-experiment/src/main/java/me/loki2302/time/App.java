package me.loki2302.time;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@SpringBootApplication
@Import(SchedulingConfig.class)
@EnableWebSocket
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class
})
public class App implements WebSocketConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(currentTimeWebSocketHandler(), "/time").withSockJS();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public CurrentTimeWebSocketHandler currentTimeWebSocketHandler() {
        return new CurrentTimeWebSocketHandler(objectMapper());
    }
}
