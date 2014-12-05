package me.loki2302;

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
public class RawWebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dummyHandler(), "/raw");
        registry.addHandler(dummyHandler(), "/sockjs").withSockJS();
        registry.addHandler(currentTimeWebSocketHandler(), "/time").withSockJS();
    }

    @Bean
    public DummyServerWebSocketHandler dummyHandler() {
        return new DummyServerWebSocketHandler();
    }

    @Bean
    public CurrentTimeWebSocketHandler currentTimeWebSocketHandler() {
        return new CurrentTimeWebSocketHandler();
    }
}
