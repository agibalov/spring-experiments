package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpringOAuth2RestTemplateTest {
    @Test
    public void canMakeARequest() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername("user1");
        resource.setPassword("user1password");
        resource.setAccessTokenUri("http://localhost:8080/oauth/token");
        resource.setClientId("MyClientId1");
        resource.setClientSecret("MyClientId1Secret");
        resource.setGrantType("password");
        resource.setScope(Arrays.asList("read"));

        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resource);
        Map<String, String> responseBody = oAuth2RestTemplate.getForObject("http://localhost:8080/", Map.class);
        assertEquals(
                "PRINCIPAL is user 'user1' ([ROLE_USER]) [clientId=MyClientId1]",
                responseBody.get("message"));
    }

    @Test
    public void canFailWithIncorrectCredentials() {
        ResourceOwnerPasswordResourceDetails resource = new ResourceOwnerPasswordResourceDetails();
        resource.setUsername("INCORRECT_USERNAME");
        resource.setPassword("user1password");
        resource.setAccessTokenUri("http://localhost:8080/oauth/token");
        resource.setClientId("MyClientId1");
        resource.setClientSecret("MyClientId1Secret");
        resource.setGrantType("password");
        resource.setScope(Arrays.asList("read"));

        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resource);
        try {
            oAuth2RestTemplate.getForObject("http://localhost:8080/", String.class);
            fail();
        } catch (OAuth2AccessDeniedException e) {
            assertEquals(OAuth2Exception.ACCESS_DENIED, e.getOAuth2ErrorCode());
        } catch (Throwable t) {
            fail();
        }
    }

    @Import(App.class)
    public static class Config {
    }
}
