package io.agibalov;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth.provider.ExtraTrustConsumerDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DummyConsumerDetailsService implements ConsumerDetailsService {
    private final Map<String, App.KnownConsumer> consumersByConsumerKeys = new HashMap<>();

    public DummyConsumerDetailsService(List<App.KnownConsumer> knownConsumers) {
        consumersByConsumerKeys.putAll(knownConsumers.stream()
                .collect(Collectors.toMap(c -> c.getConsumerKey(), c -> c)));
    }

    @Override
    public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
        App.KnownConsumer consumer = consumersByConsumerKeys.getOrDefault(consumerKey, null);
        if (consumer == null) {
            throw new OAuthException(String.format("Unknown consumer %s", consumerKey));
        }

        return new ExtraTrustConsumerDetails() {
            @Override
            public boolean isRequiredToObtainAuthenticatedToken() {
                return false;
            }

            @Override
            public String getConsumerKey() {
                return consumerKey;
            }

            @Override
            public String getConsumerName() {
                return String.format("Consumer %s", consumerKey);
            }

            @Override
            public SignatureSecret getSignatureSecret() {
                return new SharedConsumerSecretImpl(consumer.getConsumerSecret());
            }

            @Override
            public List<GrantedAuthority> getAuthorities() {
                return AuthorityUtils.NO_AUTHORITIES;
            }
        };
    }
}
