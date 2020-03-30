package io.agibalov;

import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.consumer.ProtectedResourceDetails;

import java.util.Collections;
import java.util.Map;

class OAuthConsumerKeyAndSecret implements ProtectedResourceDetails {
    private final String consumerKey;
    private final String consumerSecret;

    OAuthConsumerKeyAndSecret(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    @Override
    public String getId() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getConsumerKey() {
        return consumerKey;
    }

    @Override
    public String getSignatureMethod() {
        return "HMAC-SHA256";
    }

    @Override
    public SignatureSecret getSharedSecret() {
        return new SharedConsumerSecretImpl(consumerSecret);
    }

    @Override
    public String getRequestTokenURL() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getRequestTokenHttpMethod() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getUserAuthorizationURL() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getAccessTokenURL() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String getAccessTokenHttpMethod() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isAcceptsAuthorizationHeader() {
        return false;
    }

    @Override
    public String getAuthorizationHeaderRealm() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isUse10a() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map<String, String> getAdditionalParameters() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getAdditionalRequestHeaders() {
        return Collections.emptyMap();
    }
}
