package io.agibalov;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RefreshScope
@RestController
public class DummyController {
    // http://cloud.spring.io/spring-cloud-static/spring-cloud-consul/2.0.0.M6/single/spring-cloud-consul.html#spring-cloud-consul-config
    // loaded from consul: config/${spring.application.name}/message
    @Value("${message:default}")
    private String message;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping(path = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String hello() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("Message is: '%s'\n\n", message));

        List<String> services = discoveryClient.getServices();
        for(String service : services) {
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(service);
            for(ServiceInstance serviceInstance : serviceInstances) {
                stringBuilder.append(String.format("service=%s host=%s port=%s\n",
                        service, serviceInstance.getHost(), serviceInstance.getPort()));
            }
        }

        return stringBuilder.toString();
    }
}
