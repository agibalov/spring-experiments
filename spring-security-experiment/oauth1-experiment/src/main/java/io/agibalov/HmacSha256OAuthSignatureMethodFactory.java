package io.agibalov;

import lombok.SneakyThrows;
import org.springframework.security.oauth.common.signature.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class HmacSha256OAuthSignatureMethodFactory implements OAuthSignatureMethodFactory {
    private final static String HMAC_SHA256_METHOD_NAME = "HMAC-SHA256";

    @Override
    public OAuthSignatureMethod getSignatureMethod(
            String methodName,
            SignatureSecret signatureSecret,
            String tokenSecret) throws UnsupportedSignatureMethodException {

        if (!methodName.equals(HMAC_SHA256_METHOD_NAME)) {
            throw new UnsupportedSignatureMethodException(
                    String.format("Only %s is supported", HMAC_SHA256_METHOD_NAME));
        }

        if (!(signatureSecret instanceof SharedConsumerSecret)) {
            throw new RuntimeException(String.format("signatureSecret must be an instance of SharedConsumerSecret"));
        }

        SharedConsumerSecret sharedConsumerSecret = (SharedConsumerSecret) signatureSecret;

        return new OAuthSignatureMethod() {
            @Override
            public String getName() {
                return HMAC_SHA256_METHOD_NAME;
            }

            @SneakyThrows
            @Override
            public String sign(String signatureBaseString) {
                Mac mac = Mac.getInstance("HmacSHA256");
                SecretKeySpec secretKeySpec = new SecretKeySpec(
                        sharedConsumerSecret.getConsumerSecret().getBytes(),
                        "HmacSHA256");
                mac.init(secretKeySpec);

                byte[] signatureBytes = mac.doFinal(signatureBaseString.getBytes());

                return Base64.getEncoder().encodeToString(signatureBytes);
            }

            @Override
            public void verify(String signatureBaseString, String signature) throws InvalidSignatureException {
                if (!sign(signatureBaseString).equals(signature)) {
                    throw new InvalidSignatureException("Signatures don't match");
                }
            }
        };
    }
}
