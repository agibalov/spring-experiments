package me.loki2302.oauth;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class TwitterAuthenticationService {
    private final OAuthService twitterService;
    
    public TwitterAuthenticationService(
            String apiKey, 
            String apiSecret, 
            String callbackUri) {
        
        twitterService = new ServiceBuilder()
            .provider(TwitterApi.class)
            .apiKey(apiKey)
            .apiSecret(apiSecret)
            .callback(callbackUri)
            .build(); 
    }
    
    public String getAuthenticationUri() {
        Token requestToken = twitterService.getRequestToken();
        String authorizationUrl = twitterService.getAuthorizationUrl(requestToken);
        return authorizationUrl;
    }
    
    public OAuthToken getAccessToken(String oauthToken, String oauthVerifier) {
        Token requestToken = new Token(oauthToken, oauthVerifier);
        Verifier verifier = new Verifier(oauthVerifier);
        Token accessToken = twitterService.getAccessToken(requestToken, verifier);
        
        OAuthToken oauthAccessToken = new OAuthToken();
        oauthAccessToken.accessToken = accessToken.getToken();
        oauthAccessToken.accessTokenSecret = accessToken.getSecret();
        
        return oauthAccessToken;
    }
    
    public static class OAuthToken {
        public String accessToken;
        public String accessTokenSecret;
    }
}