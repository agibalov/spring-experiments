package me.loki2302.httplogging.logging;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class LoggingRestTemplateConfiguration {
    @Bean
    public LoggingHttpRequestResponseInterceptor loggingHttpRequestResponseInterceptor() {
        return new LoggingHttpRequestResponseInterceptor();
    }

    @Bean
    public RestTemplate restTemplate() {
        LoggingHttpRequestResponseInterceptor loggingHttpRequestResponseInterceptor = loggingHttpRequestResponseInterceptor();
        CloseableHttpClient httpClient = HttpClientBuilder
                .create()
                .addInterceptorFirst((HttpRequestInterceptor) loggingHttpRequestResponseInterceptor)
                .addInterceptorLast((HttpResponseInterceptor) loggingHttpRequestResponseInterceptor)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);

        return restTemplate;
    }
}
