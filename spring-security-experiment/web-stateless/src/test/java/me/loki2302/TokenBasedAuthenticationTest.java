package me.loki2302;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TokenBasedAuthenticationTest {
    @Autowired
    private JwtService jwtService;

    @Test
    public void itShouldSayItDoesNotKnowWhoIAmIfIDontTellIt() {
        RestTemplate restTemplate = new RestTemplate();
        NameDto nameDto = restTemplate.getForObject("http://localhost:8080/api/me", NameDto.class);
        assertNull(nameDto.name);
    }

    @Test
    public void itShouldSayWhoIAmIfITellIt() {
        NameDto nameDto = new NameDto();
        nameDto.name = "loki2302";

        RestTemplate restTemplate = new RestTemplate();
        TokenDto tokenDto = restTemplate.postForObject("http://localhost:8080/api/token", nameDto, TokenDto.class);
        assertEquals("loki2302", jwtService.getSubject(tokenDto.token));

        RequestEntity requestEntity = RequestEntity.get(URI.create("http://localhost:8080/api/me"))
                .header("Authorization", "Bearer " + tokenDto.token)
                .build();
        ResponseEntity<NameDto> meResponseEntity = restTemplate.exchange(requestEntity, NameDto.class);
        assertEquals("loki2302", meResponseEntity.getBody().name);
    }

    @Configuration
    @EnableAutoConfiguration
    public static class Config extends WebSecurityConfigurerAdapter {
        protected Config() {
            super(true);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            // Required to propagate authentication info from SecurityContextHolder to
            // HttpServletRequest. Without this, authentication info will only be available
            // via SecurityContextHolder, and things like Principal injection won't work
            http.servletApi();

            http.addFilterBefore(
                    jwtAuthorizationBearerAuthenticationFilter(),
                    UsernamePasswordAuthenticationFilter.class);
        }

        @Bean
        public JwtService jwtService() {
            return new JwtService();
        }

        @Bean
        public JwtAuthorizationBearerAuthenticationFilter jwtAuthorizationBearerAuthenticationFilter() {
            return new JwtAuthorizationBearerAuthenticationFilter();
        }

        @Bean
        public ApiController apiController() {
            return new ApiController();
        }
    }

    public static class JwtService {
        private final static String SECRET = "the very secret secret 31337";

        public String makeToken(String subject) {
            return Jwts.builder()
                    .setSubject(subject)
                    .signWith(SignatureAlgorithm.HS512, SECRET)
                    .compact();
        }

        public String getSubject(String token) {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }
    }

    public static class JwtAuthorizationBearerAuthenticationFilter extends OncePerRequestFilter {
        private final static Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationBearerAuthenticationFilter.class);

        @Autowired
        private JwtService jwtService;

        @Override
        protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException {

            String authorizationHeader = request.getHeader("Authorization");
            boolean hasToken = authorizationHeader != null && authorizationHeader.startsWith("Bearer ");
            if(hasToken) {
                String token = authorizationHeader.substring("Bearer ".length());
                Authentication authentication = getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

            if(hasToken) {
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }

        private Authentication getAuthentication(String token) {
            String subject = jwtService.getSubject(token);
            return new JwtAuthenticationToken(subject, token, AuthorityUtils.NO_AUTHORITIES);
        }

        public static class JwtAuthenticationToken extends AbstractAuthenticationToken {
            private final String username;
            private final String token;

            public JwtAuthenticationToken(String username, String token, Collection<? extends GrantedAuthority> authorities) {
                super(authorities);
                this.username = username;
                this.token = token;
            }

            @Override
            public Object getCredentials() {
                return token;
            }

            @Override
            public Object getPrincipal() {
                return username;
            }
        }
    }

    @RestController
    public static class ApiController {
        @Autowired
        private TokenBasedAuthenticationTest.JwtService jwtService;

        @RequestMapping(method = RequestMethod.POST, value = "/api/token")
        public TokenDto makeToken(@RequestBody NameDto nameDto) {
            String token = jwtService.makeToken(nameDto.name);

            TokenDto tokenDto = new TokenDto();
            tokenDto.token = token;
            return tokenDto;
        }

        @RequestMapping(method = RequestMethod.GET, value = "/api/me")
        public NameDto getMe(Principal principal) {
            NameDto nameDto = new NameDto();
            nameDto.name = principal != null ? principal.getName() : null;
            return nameDto;
        }
    }

    public static class NameDto {
        public String name;
    }

    public static class TokenDto {
        public String token;
    }
}
