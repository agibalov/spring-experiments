package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;

public class App {
    // +TODO: @PreAuthorize
    // +TODO: @PostAuthorize
    // +TODO: @PreFilter
    // +TODO: @PostFilter
    // +TODO: SpEL - trivial expression
    // +TODO: SpEL - use context beans
    // TODO: completely custom handling (like, god object that knows what is allowed and what is not)
    // +TODO: convert to JUnit tests

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Config.class, args);
        Calculator calculator = context.getBean(Calculator.class);

        UsernamePasswordAuthenticationToken t = new UsernamePasswordAuthenticationToken(
                "hello",
                "hello",
                AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(t);

        //
    }
}
