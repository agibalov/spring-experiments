package me.loki2302;

import org.rythmengine.RythmEngine;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.rythmengine.conf.RythmConfigurationKey.HOME_TEMPLATE;

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
            Map<String, Object> config = new HashMap<String, Object>();
            config.put(HOME_TEMPLATE.getKey(), "templates");
            RythmEngine rythmEngine = new RythmEngine(config);

            RythmViewResolver rythmViewResolver = new RythmViewResolver(rythmEngine);
            rythmViewResolver.setPrefix("");
            rythmViewResolver.setSuffix(".html");

            return rythmViewResolver;
        }
    }

    public static class RythmViewResolver implements ViewResolver {
        private final RythmEngine rythmEngine;
        private String prefix = "";
        private String suffix = "";

        public RythmViewResolver(RythmEngine rythmEngine) {
            this.rythmEngine = rythmEngine;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            String templateName = makeTemplateName(viewName);
            return new RythmView(rythmEngine, templateName);
        }

        private String makeTemplateName(String viewName) {
            return prefix + viewName + suffix;
        }
    }

    public static class RythmView implements View {
        private final RythmEngine rythmEngine;
        private final String templateName;

        public RythmView(RythmEngine rythmEngine, String templateName) {
            this.rythmEngine = rythmEngine;
            this.templateName = templateName;
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

            String result = rythmEngine.render(templateName, model);

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
