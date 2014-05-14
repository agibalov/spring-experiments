package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);
    }

    @ComponentScan
    @EnableAutoConfiguration
    public static class Config {
    }

    @Controller
    public static class HomeController {
        @RequestMapping("/")
        public String index(Model model) {
            model.addAttribute("currentTime", new Date());
            return "index";
        }
    }
}
