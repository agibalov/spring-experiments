package me.loki2302;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration
@Import({RawWebSocketConfig.class, StompConfig.class})
public class AppConfig {
}
