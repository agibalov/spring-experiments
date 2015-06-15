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

    @RequestMapping(value = "/version")
    @ResponseBody
    public String version() {
        Package meLoki2302Package = Package.getPackage("me.loki2302");
        String version = meLoki2302Package.getImplementationVersion();
        if(version == null) {
            version = "no version info";
        }

        return String.format("%s (%s)", meLoki2302Package.getName(), version);
    }
}
