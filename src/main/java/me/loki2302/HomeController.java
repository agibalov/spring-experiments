package me.loki2302;

import java.io.IOException;
import java.net.URISyntaxException;

import me.loki2302.oauth.FacebookAuthenticationService;
import me.loki2302.oauth.FacebookUserInfo;
import me.loki2302.oauth.GoogleAuthenticationService;
import me.loki2302.oauth.GoogleUserInfo;
import me.loki2302.oauth.TwitterAuthenticationService2;
import me.loki2302.oauth.TwitterAuthenticationService2.OAuthToken2;
import me.loki2302.oauth.TwitterUserInfo;

import org.apache.http.client.ClientProtocolException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {    
    private final GoogleAuthenticationService googleAuthenticationService = new GoogleAuthenticationService(
            "330741531920.apps.googleusercontent.com", 
            "R21tppN-oV9bAqg-Sgp5tTNg", 
            "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email", 
            "http://localhost:8080/googleCallback");
    
    private final FacebookAuthenticationService facebookAuthenticationService = new FacebookAuthenticationService(
            "541155062638962", 
            "dc4e49157cba8e4141bd08a6ea95021f", 
            "email", 
            "http://localhost:8080/facebookCallback");
    
    private final TwitterAuthenticationService2 twitterAuthenticationService = new TwitterAuthenticationService2(
            "JRBEmQEBhV1B5gJOvzA3ag",
            "VlB7e9wA0WIRmVOVzGpLQqgzUjAyklezXdWCXTSM21Y",
            "http://localhost:8080/twitterCallback");
    
    @RequestMapping(value = "/", method = RequestMethod.GET)    
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public View google() {        
        String authUri = googleAuthenticationService.getAuthenticationUri();
        return new RedirectView(authUri);
    }
    
    @RequestMapping(value = "/googleCallback", method = RequestMethod.GET)
    public String googleCallback(
            @RequestParam(value = "code", required = false) String code, 
            @RequestParam(value = "error", required = false) String error,
            Model model) throws ClientProtocolException, URISyntaxException, IOException {
        
        model.addAttribute("provider", "Google");
        
        if(error != null) {
            if(error.equals("access_denied")) {
                System.out.println("user cancelled");
            } else {
                System.out.printf("error: %s\n", error);
            }
        } else {            
            String accessToken = googleAuthenticationService.getAccessToken(code);
            GoogleUserInfo googleUserInfo = googleAuthenticationService.getUserInfo(accessToken);
            
            model.addAttribute("token", accessToken);
            model.addAttribute("userData", googleUserInfo.toString());
        }
        
        return "index";
    }
    
    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    public View facebook() {        
        String authUri = facebookAuthenticationService.getAuthenticationUri();
        return new RedirectView(authUri);
    }
    
    @RequestMapping(value = "/facebookCallback", method = RequestMethod.GET)
    public String facebookCallback(
            @RequestParam(value = "code", required = false) String code, 
            @RequestParam(value = "error", required = false) String error,
            Model model) {
        
        model.addAttribute("provider", "Facebook");
        
        if(error != null) {
            if(error.equals("access_denied")) {
                System.out.println("user cancelled");
            } else {
                System.out.printf("error: %s\n", error);
            }
        } else {
            String accessToken = facebookAuthenticationService.getAccessToken(code);
            FacebookUserInfo facebookUserInfo = facebookAuthenticationService.getUserInfo(accessToken);
            
            model.addAttribute("token", accessToken);
            model.addAttribute("userData", facebookUserInfo.toString());
        }
        
        return "index";
    }
    
    @RequestMapping(value = "/twitter", method = RequestMethod.GET)
    public View twitter() {
        String authUri = twitterAuthenticationService.getAuthenticationUri();
        return new RedirectView(authUri);
    }
    
    @RequestMapping(value = "/twitterCallback", method = RequestMethod.GET)
    public String twitterCallback(
            @RequestParam(value = "oauth_token", required = false) String oauthToken,
            @RequestParam(value = "oauth_verifier", required = false) String oauthVerifier,
            @RequestParam(value = "denied", required = false) String denied,
            Model model) {
        
        model.addAttribute("provider", "Twitter");
        
        if(denied != null && !denied.equals("")) {
            System.out.println("user cancelled");
        } else {            
            OAuthToken2 accessToken = twitterAuthenticationService.getAccessToken(oauthToken, oauthVerifier);            
            TwitterUserInfo twitterUserInfo = twitterAuthenticationService.getUserInfo(accessToken);
            
            model.addAttribute("token", String.format("'%s'/'%s'", 
                    accessToken.accessToken, 
                    accessToken.accessTokenSecret));
            model.addAttribute("userData", twitterUserInfo.toString());
        }
        
        return "index";
    }
}
