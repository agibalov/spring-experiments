package me.loki2302;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.connect.web.ConnectSupport;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.userinfo.GoogleUserInfo;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

        @Bean
        public FacebookConnectionFactory facebookConnectionFactory() {
            FacebookConnectionFactory facebookConnectionFactory = new FacebookConnectionFactory(
                    "470358236410054",
                    "aabe43ffb4e3f2e2c0c3e8502d6db530");

            facebookConnectionFactory.setScope("email");

            return facebookConnectionFactory;
        }
    }

    @Controller
    public static class HomeController {
        private final static Logger log = LoggerFactory.getLogger(HomeController.class);

        @Autowired
        private GoogleConnectionFactory googleConnectionFactory;

        @Autowired
        private FacebookConnectionFactory facebookConnectionFactory;

        @RequestMapping("/")
        public String index(Model model) {
            return "index";
        }

        @RequestMapping("/{provider}")
        public View authenticate(
                @PathVariable String provider,
                NativeWebRequest nativeWebRequest) {

            String callbackUrl = makeCallbackUrl(provider);
            log.info("Callback URL is {}", callbackUrl);

            ConnectSupport connectSupport = new ConnectSupport();
            connectSupport.setCallbackUrl(callbackUrl);

            ConnectionFactory connectionFactory = getConnectionFactoryByProviderName(provider);

            String authorizeUrl = connectSupport.buildOAuthUrl(
                    connectionFactory,
                    nativeWebRequest);

            log.info("Authorize URL is {}", authorizeUrl);

            return new RedirectView(authorizeUrl);
        }

        @RequestMapping(value = "/{provider}/callback", method = RequestMethod.GET, params = "code")
        public String callbackSuccess(
                @PathVariable String provider,
                Model model,
                NativeWebRequest request) {

            String callbackUrl = makeCallbackUrl(provider);
            log.info("Callback URL is {}", callbackUrl);

            ConnectSupport connectSupport = new ConnectSupport();
            connectSupport.setCallbackUrl(callbackUrl);
            try {
                ConnectionFactory<?> connectionFactory = getConnectionFactoryByProviderName(provider);
                Connection<?> connection = connectSupport.completeConnection(
                        (OAuth2ConnectionFactory<?>)connectionFactory,
                        request);

                if(connectionFactory instanceof GoogleConnectionFactory) {
                    extendModelWithGoogleDetails(model, (Connection<Google>)connection);
                } else if(connectionFactory instanceof FacebookConnectionFactory) {
                    extendModelWithFacebookDetails(model, (Connection<Facebook>)connection);
                } else {
                    throw new RuntimeException("Unknown connectionFactory " + connectionFactory);
                }
            } catch(Exception e) {
                model.addAttribute("error", e.getMessage());
            }

            return "index";
        }

        private static void extendModelWithGoogleDetails(
                Model model,
                Connection<Google> googleConnection) {

            model.addAttribute("name", googleConnection.getDisplayName());
            model.addAttribute("profileUrl", googleConnection.getProfileUrl());
            model.addAttribute("imageUrl", googleConnection.getImageUrl());

            GoogleUserInfo googleUserInfo = googleConnection.getApi().userOperations().getUserInfo();
            model.addAttribute("email", googleUserInfo.getEmail());
        }

        private static void extendModelWithFacebookDetails(
                Model model,
                Connection<Facebook> facebookConnection) {

            model.addAttribute("name", facebookConnection.getDisplayName());
            model.addAttribute("profileUrl", facebookConnection.getProfileUrl());
            model.addAttribute("imageUrl", facebookConnection.getImageUrl());

            FacebookProfile facebookProfile = facebookConnection.getApi().userOperations().getUserProfile();
            model.addAttribute("email", facebookProfile.getEmail());
        }

        @RequestMapping(value = "/{provider}/callback", method = RequestMethod.GET, params = "error")
        public String callbackError(
                @PathVariable String provider,
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

        private static String makeCallbackUrl(String provider) {
            String callbackUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/{provider}/callback")
                    .buildAndExpand(provider)
                    .toUriString();

            return callbackUrl;
        }

        private ConnectionFactory<?> getConnectionFactoryByProviderName(String provider) {
            if(provider.equals("google")) {
                return googleConnectionFactory;
            }

            if(provider.equals("facebook")) {
                return facebookConnectionFactory;
            }

            throw new RuntimeException("Unknown provider " + provider);
        }
    }
}
