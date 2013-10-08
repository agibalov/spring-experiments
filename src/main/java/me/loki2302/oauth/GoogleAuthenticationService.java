package me.loki2302.oauth;

import java.io.IOException;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class GoogleAuthenticationService {
    private final static String GoogleAccessCodeServiceEndpointUri = "https://accounts.google.com/o/oauth2/auth";
    private final static String GoogleAccessTokenServiceEndpointUri = "https://accounts.google.com/o/oauth2/token";
    
    private final static OAuth2Template oauth2Template = new OAuth2Template();
    
    private final String clientId;
    private final String clientSecret; 
    private final String scope;
    private final String callbackUri;
    
    public GoogleAuthenticationService(
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
                GoogleAccessCodeServiceEndpointUri, 
                clientId, 
                callbackUri, 
                scope);
    }
    
    public String getAccessToken(String code) {
        String accessTokenResponse;
        try {
            accessTokenResponse = oauth2Template.getAccessTokenResponse(
                    GoogleAccessTokenServiceEndpointUri, 
                    code, 
                    clientId, 
                    clientSecret, 
                    callbackUri);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        GoogleAuthenticationService.GoogleAccessTokenResponse googleAccessTokenResponse;
        try {
            googleAccessTokenResponse = objectMapper.readValue(
                    accessTokenResponse, 
                    GoogleAccessTokenResponse.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return googleAccessTokenResponse.accessToken;
    }
    
    public GoogleUserInfo getUserInfo(String accessToken) {    
        HttpGet getMeRequest = new HttpGet("https://www.googleapis.com/userinfo/v2/me");
        getMeRequest.addHeader("Authorization", String.format("Bearer %s", accessToken));
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response;
        try {
            response = client.execute(getMeRequest);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        String responseString;
        try {
            responseString = IOUtils.toString(response.getEntity().getContent());
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(responseString, GoogleUserInfo.class);
        } catch (JsonParseException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class GoogleAccessTokenResponse {        
        @JsonProperty("access_token") public String accessToken;
        @JsonProperty("token_type") public String tokenType;
        @JsonProperty("expires_in") public int expiresIn;
        @JsonProperty("id_token") public String idToken;
    }
}