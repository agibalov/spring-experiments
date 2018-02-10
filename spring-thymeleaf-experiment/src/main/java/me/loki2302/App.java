package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Controller
    public static class HomeController {
        @RequestMapping("/")
        public String index(Model model) {
            model.addAttribute("currentTime", new Date());
            return "index";
        }

        @RequestMapping("/page2")
        public String page2(Model model) {
            List<Person> people = new ArrayList<Person>();
            for(int i = 0; i < 10; ++i) {
                people.add(new Person(i + 1, String.format("John #%d", i + 1)));
            }

            model.addAttribute("people", people);

            return "page2";
        }
    }

    public static class Person {
        public int id;
        public String name;

        public Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
