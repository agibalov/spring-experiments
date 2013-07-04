package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealNotificationServiceConfiguration {
    @Autowired
    private Settings settings;
    
    @Bean
    public NotificationService notificationService() {
        return new RealNotificationService(settings.getNotifyAs());
    }
}