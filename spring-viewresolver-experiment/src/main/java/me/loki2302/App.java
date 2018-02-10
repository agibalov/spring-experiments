package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);
    }

    @Configuration
    @ComponentScan
    @EnableAutoConfiguration
    public static class Config {
        @Bean
        public ViewResolver viewResolver() {
            return new MyViewResolver();
        }
    }

    public static class MyViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            return new MyView(viewName);
        }
    }

    public static class MyView implements View {
        private final String viewName;

        public MyView(String viewName) {
            this.viewName = viewName;
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public void render(
                Map<String, ?> model,
                HttpServletRequest request,
                HttpServletResponse response) throws Exception {

            response.getWriter().format("ViewName: %s\nIP: %s\nModel: %s\n",
                    viewName,
                    request.getRemoteAddr(),
                    model.get("time"));
        }
    }

    @Controller
    public static class HomeController {
        @RequestMapping("/")
        public String index(Model model) {
            model.addAttribute("time", new Date());
            return "index";
        }
    }
}
