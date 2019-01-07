package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public FilterRegistrationBean rewriteFilterConfig() {
        FilterRegistrationBean reg = new FilterRegistrationBean();
        reg.setName("rewriteFilter");
        reg.setFilter(new UrlRewriteFilter());
        reg.addInitParameter("confPath", "urlrewrite.xml");
        return reg;
    }

    @RestController
    public static class HomeController {
        @RequestMapping("/")
        public String index() {
            return "hello there";
        }
    }
}
