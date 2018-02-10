package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/sign-in")
public class SignInController {
	
	private final static Logger logger = LoggerFactory.getLogger(SignInController.class);
	
    @RequestMapping(method = RequestMethod.GET)
    public String signIn(Model model) {
        model.addAttribute("signInModel", new SignInModel());
        return "signin";
    }
        
    @RequestMapping(method = RequestMethod.POST)
    public String signIn(
            Model model,
            @Validated @ModelAttribute("signInModel") SignInModel signInModel, 
            BindingResult bindingResult) {
        
        logger.info("New sign in request");
        
        if(bindingResult.hasErrors()) {
        	logger.warn("There are errors");
            for(ObjectError e : bindingResult.getAllErrors()) {
            	logger.warn("error: {}", e);
            }
        } else {
        	logger.info("There are NO errors");
        }
        
        logger.info("Email: {}, Password: {}", signInModel.getEmail(), signInModel.getPassword());
        
        String email = signInModel.getEmail();
        if(email.equalsIgnoreCase("a@a")) {            
            model.addAttribute("message", "omg wtf bbq");
        }
                
        return "signin";
    }
    
    public static class SignInModel {
        @Email
        private String email;
        
        @Password
        private String password;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }            
    }
}