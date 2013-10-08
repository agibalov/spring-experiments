package me.loki2302.oauth;

import java.io.IOException;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;

public class FacebookAuthenticationService {    
    private final static String FacebookAccessCodeServiceEndpointUri = "https://www.facebook.com/dialog/oauth";
    private final static String FacebookAccessTokenServiceEndpointUri = "https://graph.facebook.com/oauth/access_token";
    
    private final static OAuth2Template oauth2Template = new OAuth2Template();
    
    private final String clientId;
    private final String clientSecret; 
    private final String scope;
    private final String callbackUri;
    
    public FacebookAuthenticationService(
            String clientId, 
            String clientSecret, 
            String scope, 
            String callbackUri) {
        
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
        this.callbackUri = callbackUri;
    }
    
    public String getAuthenticationUri() {
        return oauth2Template.makeAuthenticationUri(
                FacebookAccessCodeServiceEndpointUri, 
                clientId, 
                callbackUri, 
                scope);
    }
    
    public String getAccessToken(String code) {
        String accessTokenResponse;
        try {
            accessTokenResponse = oauth2Template.getAccessTokenResponse(
                    FacebookAccessTokenServiceEndpointUri, 
                    code, 
                    clientId, 
                    clientSecret, 
                    callbackUri);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        List<NameValuePair> responseNameValuePairs = URLEncodedUtils.parse(accessTokenResponse, Consts.UTF_8);
        for(NameValuePair nameValuePair : responseNameValuePairs) {
            if(nameValuePair.getName().equals("access_token")) {
                return nameValuePair.getValue();
            }
        }
        
        throw new RuntimeException("didn't expect to get this far");
    }
}
