package me.loki2302.oauth;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

public class OAuth2Template {
    public String makeAuthenticationUri(
            String accessCodeServiceEndpointUri, 
            String clientId, 
            String callbackUri, 
            String scope) {
        
        try {
            return new URIBuilder(accessCodeServiceEndpointUri)
                .addParameter("response_type", "code")
                .addParameter("client_id", clientId)
                .addParameter("redirect_uri", callbackUri)
                .addParameter("scope", scope)
                .build()
                .toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getAccessTokenResponse(
            String accessTokenServiceEndpointUri, 
            String code, 
            String clientId, 
            String clientSecret, 
            String callbackUri) throws ClientProtocolException, IOException {
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();            
        params.add(new BasicNameValuePair("code", code));
        params.add(new BasicNameValuePair("client_id", clientId));
        params.add(new BasicNameValuePair("client_secret", clientSecret));
        params.add(new BasicNameValuePair("redirect_uri", callbackUri));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        
        HttpPost post = new HttpPost(accessTokenServiceEndpointUri);
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response = client.execute(post);
        String responseString = IOUtils.toString(response.getEntity().getContent());
        
        return responseString;
    }
}