package com.loki2302;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WebAppController {
	
	@RequestMapping
	public String index() {
		return "index";
	}
}
