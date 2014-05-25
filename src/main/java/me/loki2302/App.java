package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.social.connect.Connection;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

public class App {
    public static void main(String[] args) {
        SpringApplication.run(Config.class, args);
    }

    @EnableAutoConfiguration
    @ComponentScan
    public static class Config {
        @Bean
        public GoogleConnectionFactory googleConnectionFactory() {
            GoogleConnectionFactory googleConnectionFactory = new GoogleConnectionFactory(
                    "330741531920.apps.googleusercontent.com",
                    "R21tppN-oV9bAqg-Sgp5tTNg");

            googleConnectionFactory.setScope(
                    "https://www.googleapis.com/auth/userinfo.profile " +
                    "https://www.googleapis.com/auth/userinfo.email");

            return googleConnectionFactory;
        }
    }

    @Controller
    public static class HomeController {
        private final static Logger log = LoggerFactory.getLogger(HomeController.class);

        @Autowired
        private GoogleConnectionFactory googleConnectionFactory;

        @RequestMapping("/")
        public String index(Model model) {
            return "index";
        }

        @RequestMapping("/google")
        public View google() {
            String callbackUrl = makeGoogleCallbackUrl();

            log.info("Callback URL is {}", callbackUrl);

            OAuth2Operations oAuth2Operations = googleConnectionFactory.getOAuthOperations();
            OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
            oAuth2Parameters.setRedirectUri(callbackUrl);
            oAuth2Parameters.setScope(googleConnectionFactory.getScope());
            String authorizeUrl = oAuth2Operations.buildAuthorizeUrl(oAuth2Parameters);

            log.info("Authorize URL is {}", authorizeUrl);

            return new RedirectView(authorizeUrl);
        }

        @RequestMapping(value = "/googleCallback", method = RequestMethod.GET, params = "code")
        public String googleCallback(Model model, NativeWebRequest request) {
            String code = request.getParameter("code");
            String callbackUrl = makeGoogleCallbackUrl();
            try {
                AccessGrant accessGrant = googleConnectionFactory
                        .getOAuthOperations()
                        .exchangeForAccess(code, callbackUrl, null);
                Connection connection = googleConnectionFactory.createConnection(accessGrant);
                model.addAttribute("name", connection.getDisplayName());
                model.addAttribute("profileUrl", connection.getProfileUrl());
                model.addAttribute("imageUrl", connection.getImageUrl());

                GoogleTemplate googleTemplate = new GoogleTemplate(accessGrant.getAccessToken());
                GoogleUserInfo googleUserInfo = googleTemplate.userOperations().getUserInfo();
                model.addAttribute("email", googleUserInfo.getEmail());
            } catch (HttpClientErrorException e) {
                model.addAttribute("error", e.getMessage());
            }

            return "index";
        }

        @RequestMapping(value = "/googleCallback", method = RequestMethod.GET, params = "error")
        public String googleErrorCallback(
                Model model,
                @RequestParam("error") String error,
                @RequestParam(value = "error_description", required = false) String errorDescription,
                @RequestParam(value = "error_uri", required = false) String errorUri) {

            if(error != null) {
                model.addAttribute("error", error);
            }

            if(errorDescription != null) {
                model.addAttribute("errorDescription", errorDescription);
            }

            if(errorUri != null) {
                model.addAttribute("errorUri", errorUri);
            }

            return "index";
        }

        private static String makeGoogleCallbackUrl() {
            String callbackUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/googleCallback")
                    .buildAndExpand()
                    .toUriString();

            return callbackUrl;
        }
    }
}
