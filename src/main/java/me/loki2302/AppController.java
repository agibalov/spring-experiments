package me.loki2302;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AppController {
    @Autowired
    private AppProperties appProperties;

    @RequestMapping(value = "/")
    @ResponseBody
    public String index() {
        return appProperties.getMessage();
    }
}
