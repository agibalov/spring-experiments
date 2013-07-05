package me.loki2302.notifications;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DummyNotificationServiceConfiguration {
    public DummyNotificationServiceConfiguration() {
        System.out.println("DummyNotificationServiceConfiguration constructed");
    }
    
    @Bean
    public NotificationService notificationService() {
        return new DummyNotificationService();
    }
}