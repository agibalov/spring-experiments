package me.loki2302;

import org.apache.commons.io.IOUtils;
import org.rythmengine.Rythm;
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
import java.io.IOException;
import java.io.InputStream;
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
            return new RythmViewResolver();
        }
    }

    public static class RythmViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            String templateResourceName = String.format("/%s.html", viewName);
            String template = loadTemplateFromResource(templateResourceName);
            return new RythmView(template);
        }

        private static String loadTemplateFromResource(String templateResourceName) throws IOException {
            InputStream inputStream = null;
            try {
                inputStream = RythmView.class.getResourceAsStream(templateResourceName);
                return IOUtils.toString(inputStream);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    public static class RythmView implements View {
        private final String template;

        public RythmView(String template) {
            this.template = template;
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public void render(
                Map<String, ?> model,
                HttpServletRequest request,
                HttpServletResponse response) throws Exception {

            String result = Rythm.render(template, model);

            response.getWriter().append(result);
        }
    }

    @Controller
    public static class HomeController {
        @RequestMapping("/")
        public String index(Model model) {
            model.addAttribute("currentTime", new Date());
            model.addAttribute("message", "hello there");
            return "index";
        }
    }
}
