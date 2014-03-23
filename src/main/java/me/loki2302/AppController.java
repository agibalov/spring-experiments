package me.loki2302;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController {
    @RequestMapping(value = "/")
    @ResponseBody
    public String index() {
        return "hello";
    }
}
