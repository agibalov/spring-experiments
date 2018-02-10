package me.loki2302.controllers;

import java.util.Date;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {    
    @RequestMapping(value = "/", method = RequestMethod.GET)    
    public String index() {        
        return "home/index";
    }
    
    @RequestMapping(value = "/time", method = RequestMethod.GET)    
    public String time(Model model) {
        model.addAttribute("currentTime", new Date());
        return "home/time";
    }
}