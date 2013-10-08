package me.loki2302;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.builder.api.GoogleApi;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    private final OAuthService googleService = new ServiceBuilder()
        .provider(GoogleApi.class)
        .apiKey("330741531920.apps.googleusercontent.com")
        .apiSecret("R21tppN-oV9bAqg-Sgp5tTNg")
        .scope("https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email")
        .callback("http://localhost:8080/googleCallback")        
        .build();
    
    private final OAuthService facebookService = new ServiceBuilder()
        .provider(FacebookApi.class)
        .apiKey("541155062638962")
        .apiSecret("dc4e49157cba8e4141bd08a6ea95021f")
        .scope("email")
        .callback("http://localhost:8080/facebookCallback")
        .build();
    
    private final OAuthService twitterService = new ServiceBuilder()
        .provider(TwitterApi.class)
        .apiKey("JRBEmQEBhV1B5gJOvzA3ag")
        .apiSecret("VlB7e9wA0WIRmVOVzGpLQqgzUjAyklezXdWCXTSM21Y")
        .callback("http://localhost:8080/twitterCallback")
        .build();    
    
    @RequestMapping(value = "/", method = RequestMethod.GET)    
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public View authenticateWithGoogle() {
        Token requestToken = googleService.getRequestToken();
        String authorizationUrl = googleService.getAuthorizationUrl(requestToken);
        return new RedirectView(authorizationUrl);
    }
    
    @RequestMapping(value = "/googleCallback", method = RequestMethod.GET)
    public String googleCallback(
            @RequestParam(value = "oauth_token", required = false) String oauthToken, 
            @RequestParam(value = "oauth_verifier", required = false) String oauthVerifier,
            Model model) throws JsonParseException, JsonMappingException, IOException {
        
        AuthenticationResult result = new AuthenticationResult();
        result.Provider = "Google";
        
        Token requestToken = new Token(oauthToken, oauthVerifier);
        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = googleService.getAccessToken(requestToken, verifier);
        
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://www.googleapis.com/oauth2/v1/userinfo");
        googleService.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println(response.getBody());
        
        ObjectMapper mapper = new ObjectMapper();
        GoogleMe googleUser = mapper.readValue(response.getBody(), GoogleMe.class);
        
        result.Message = "OK";
        result.UserDetailsJson = googleUser.toString();
        
        return "index";
    }
    
    public static class GoogleMe {        
    }
    
    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    public View authenticateWithFacebook() {
        String authorizationUrl = facebookService.getAuthorizationUrl(null);
        return new RedirectView(authorizationUrl);
    }
    
    @RequestMapping(value = "/facebookCallback", method = RequestMethod.GET)
    public String facebookCallback(
            @RequestParam(value = "code", required = false) String code, 
            @RequestParam(value = "error", required = false) String error, 
            Model model) throws JsonParseException, JsonMappingException, IOException {
        
        AuthenticationResult result = new AuthenticationResult();
        result.Provider = "Facebook";
        
        if(error != null) {
            if(error.equals("access_denied")) {
                result.Message = "User cancelled";
            } else {
                result.Message = String.format("Something bad happened: %s", error);                        
            }
        } else {        
            Verifier verifier = new Verifier(code);
            Token accessToken = facebookService.getAccessToken(null, verifier);                
            
            OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
            facebookService.signRequest(accessToken, request);
            Response response = request.send();
            
            ObjectMapper mapper = new ObjectMapper();
            FacebookMe facebookUser = mapper.readValue(response.getBody(), FacebookMe.class);                        
            
            result.Message = "OK";
            result.UserDetailsJson = facebookUser.toString();            
        }
        
        model.addAttribute("result", result);
        
        return "index";
    }
    
    @RequestMapping(value = "/twitter", method = RequestMethod.GET)
    public View authenticateWithTwitter() {
        Token requestToken = twitterService.getRequestToken();
        String authorizationUrl = twitterService.getAuthorizationUrl(requestToken);
        return new RedirectView(authorizationUrl);
    }
    
    @RequestMapping(value = "/twitterCallback", method = RequestMethod.GET)
    public String twitterCallback(
            @RequestParam(value = "oauth_token", required = false) String oauthToken, 
            @RequestParam(value = "oauth_verifier", required = false) String oauthVerifier,
            @RequestParam(value = "denied", required = false) String denied,
            Model model) throws JsonParseException, JsonMappingException, IOException {
        
        AuthenticationResult result = new AuthenticationResult();
        result.Provider = "Twitter";
        
        if(denied != null && !denied.equals("")) {
            result.Message = "User cancelled";
        } else {                
            Token requestToken = new Token(oauthToken, oauthVerifier);
            Verifier verifier = new Verifier(oauthVerifier);
            Token accessToken = twitterService.getAccessToken(requestToken, verifier);
            
            OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.twitter.com/1.1/account/verify_credentials.json");
            twitterService.signRequest(accessToken, request);
            Response response = request.send();
            
            ObjectMapper mapper = new ObjectMapper();
            TwitterMe twitterUser = mapper.readValue(response.getBody(), TwitterMe.class);
            
            result.Message = "OK";
            result.UserDetailsJson = twitterUser.toString();
        }
        
        model.addAttribute("result", result);
        
        return "index";        
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FacebookMe {
        @JsonProperty("id") public String Id;
        @JsonProperty("name") public String Name;
        @JsonProperty("first_name") public String FirstName;
        @JsonProperty("last_name") public String LastName;
        @JsonProperty("link") public String Link;
        @JsonProperty("username") public String UserName;
        @JsonProperty("gender") public String Gender;
        @JsonProperty("email") public String Email;
        @JsonProperty("timezone") public String Timezone;
        @JsonProperty("locale") public String Locale;
        @JsonProperty("verified") public boolean Verified;
        @JsonProperty("updated_time") public String UpdatedTime;
                
        @Override
        public String toString() {
            return JsonUtils.asJson(this);
        }
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TwitterMe {
        @JsonProperty("id") public String Id;
        @JsonProperty("name") public String Name;
        @JsonProperty("screen_name") public String ScreenName;
        @JsonProperty("url") public String Url;
        @JsonProperty("followers_count") public int FollowersCount;
        @JsonProperty("friends_count") public int FriendsCount;
        @JsonProperty("time_zone") public String TimeZone;
        @JsonProperty("verified") public boolean Verified;
        @JsonProperty("profile_image_url") public String ProfileImageUrl;

        @Override
        public String toString()
        {
            return JsonUtils.asJson(this);
        }
    }    
    
    public static class JsonUtils {
        private final static ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
        
        public static String asJson(Object o) {
            try {
                return objectWriter.writeValueAsString(o);
            } catch (JsonGenerationException e) {
                return "FAIL";
            } catch (JsonMappingException e) {
                return "FAIL";
            } catch (IOException e) {
                return "FAIL";
            }
        }
    }
}
