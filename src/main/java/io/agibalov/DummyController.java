package io.agibalov;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
public class DummyController {
    // http://cloud.spring.io/spring-cloud-static/spring-cloud-consul/2.0.0.M6/single/spring-cloud-consul.html#spring-cloud-consul-config
    // loaded from consul: config/dummy/message
    @Value("${message:default}")
    private String message;

    @GetMapping("/")
    public String hello() {
        return String.format("Message is: '%s'", message);
    }
}
