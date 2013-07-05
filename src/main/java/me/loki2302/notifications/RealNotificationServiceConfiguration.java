package me.loki2302.notifications;

import me.loki2302.Settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RealNotificationServiceConfiguration {
    @Autowired
    private Settings settings;
    
    public RealNotificationServiceConfiguration() {
        System.out.println("RealNotificationServiceConfiguration constructed");
    }
    
    @Bean
    public NotificationService notificationService() {
        return new RealNotificationService(settings.getNotifyAs());
    }
}