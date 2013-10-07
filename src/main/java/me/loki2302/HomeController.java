package me.loki2302;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
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
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {
    private final OAuthService googleService = new ServiceBuilder()
        .provider(GoogleApi.class)
        .apiKey("TODO")
        .apiSecret("TODO")
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
        .apiKey("TODO")
        .apiSecret("TODO")
        .build();
    
    
    @RequestMapping(value = "/", method = RequestMethod.GET)    
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/google", method = RequestMethod.GET)
    public View authenticateWithGoogle() {
        throw new RuntimeException();
    }
    
    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    public View authenticateWithFacebook() {
        String authorizationUrl = facebookService.getAuthorizationUrl(null);
        return new RedirectView(authorizationUrl);
    }
    
    @RequestMapping(value = "/facebookCallback", method = RequestMethod.GET)
    public String facebookCallback(String code, Model model) throws JsonParseException, JsonMappingException, IOException {        
        Verifier verifier = new Verifier(code);
        Token accessToken = facebookService.getAccessToken(null, verifier);                
        
        OAuthRequest request = new OAuthRequest(Verb.GET, "https://graph.facebook.com/me");
        facebookService.signRequest(accessToken, request);
        Response response = request.send();
        System.out.printf("Response (%d): %s\n", response.getCode(), response.getBody());
        
        ObjectMapper mapper = new ObjectMapper();
        FacebookMe u = mapper.readValue(response.getBody(), FacebookMe.class);
        
        System.out.println(u);
        
        AuthenticationResult result = new AuthenticationResult();
        result.UserDetailsJson = u.toString();
        model.addAttribute("result", result);
        
        return "index";
    }
    
    @RequestMapping(value = "/twitter", method = RequestMethod.GET)
    public View authenticateWithTwitter() {
        throw new RuntimeException();
    }
    
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
            ObjectWriter objectWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
            try {
                return objectWriter.writeValueAsString(this);
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
