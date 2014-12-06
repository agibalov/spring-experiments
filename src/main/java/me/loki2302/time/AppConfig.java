package me.loki2302.time;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@Import(SchedulingConfig.class)
@EnableWebSocket
@EnableAutoConfiguration
public class AppConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(currentTimeWebSocketHandler(), "/time").withSockJS();
    }

    @Bean
    public CurrentTimeWebSocketHandler currentTimeWebSocketHandler() {
        return new CurrentTimeWebSocketHandler();
    }
}
