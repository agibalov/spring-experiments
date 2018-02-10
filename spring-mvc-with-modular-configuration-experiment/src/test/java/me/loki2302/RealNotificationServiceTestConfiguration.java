package me.loki2302;

import me.loki2302.notifications.RealNotificationServiceConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

@Configuration
@ComponentScan(value = "me.loki2302", excludeFilters = { 
        @ComponentScan.Filter(Controller.class), 
        @ComponentScan.Filter(Configuration.class) })
@Import(RealNotificationServiceConfiguration.class)
public class RealNotificationServiceTestConfiguration {
    @Bean
    public Settings settings() {
        Settings settings = new Settings();
        settings.setNotifyAs("test");
        return settings;
    }
}
