package me.loki2302;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyNotificationServiceConfiguration {
    @Bean
    public NotificationService notificationService() {
        return new DummyNotificationService();
    }
}