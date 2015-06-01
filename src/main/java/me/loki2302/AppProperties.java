package me.loki2302;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// priorities:
// 1. ./spring-boot-experiment --app.message="from command line"
// 2. export APP_MESSAGE="from env" && ./spring-boot-experiment
// 3. echo "app.message: local yaml" > application.yml && ./spring-boot-experiment
// 4. if none of the above, the config comes from built-in applicaiton.yml

@Component
@ConfigurationProperties("app")
public class AppProperties {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
