package me.loki2302;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    // +TODO: @PreAuthorize
    // +TODO: @PostAuthorize
    // +TODO: @PreFilter
    // +TODO: @PostFilter
    // +TODO: SpEL - trivial expression
    // +TODO: SpEL - use context beans
    // TODO: completely custom handling (like, god object that knows what is allowed and what is not)
    // TODO: convert to JUnit tests

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Config.class, args);
        Calculator calculator = context.getBean(Calculator.class);

        UsernamePasswordAuthenticationToken t = new UsernamePasswordAuthenticationToken(
                "hello",
                "hello",
                AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(t);

        try {
            System.out.printf("%d [%s]\n", calculator.add(9, 2), calculator); // OK
            System.out.printf("%d [%s]\n", calculator.add(11, 2), calculator); // throws
        } catch(AccessDeniedException e) {
            System.out.println("Access denied!");
        }

        try {
            System.out.printf("%d [%s]\n", calculator.div(11, 2), calculator); // OK
            System.out.printf("%d [%s]\n", calculator.div(11, 13), calculator); // throws
        } catch(AccessDeniedException e) {
            System.out.println("Access denied!");
        }

        try {
            System.out.printf("%d\n", calculator.sub(1, 2)); // OK
            System.out.printf("%d\n", calculator.sub(10, 5)); // throws
        } catch(AccessDeniedException e) {
            System.out.println("Access denied!");
        }

        System.out.printf("%d\n", calculator.add(new ArrayList<Integer>(Arrays.asList(2, 6, 4)))); // OK
        System.out.printf("%d\n", calculator.add(new ArrayList<Integer>(Arrays.asList(2, 6, 4, 1)))); // 1 is ignored as it's odd

        System.out.printf("%d\n", calculator.doublify(Arrays.asList(1, 2, 5)).size());
        System.out.printf("%d\n", calculator.doublify(Arrays.asList(1, 2, 5, 3)).size()); // 3 is ignored
    }

    @Configuration
    @ComponentScan
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class Config {
        @Bean
        AuthenticationManager authenticationManager(AuthenticationManagerBuilder b) throws Exception {
            return b.getOrBuild();
        }
    }

    @Component
    public static class Calculator {
        @PreAuthorize("#x < 10")
        public int add(int x, int y) {
            return x + y;
        }

        @PreAuthorize("@divSecurityDecider.canDivideBy(#y)")
        public int div(int x, int y) {
            return x / y;
        }

        @PostAuthorize("returnObject != 5")
        public int sub(int x, int y) {
            return x - y;
        }

        @PreFilter("filterObject % 2 == 0")
        public int add(List<Integer> values) {
            int sum = 0;
            for(int x : values) {
                sum += x;
            }
            return sum;
        }

        @PostFilter("filterObject % 3 != 0")
        public List<Integer> doublify(List<Integer> values) {
            List<Integer> result = new ArrayList<Integer>();
            for(int x : values) {
                result.add(x * 2);
            }
            return result;
        }
    }

    @Component("divSecurityDecider")
    public static class DivSecurityDecider {
        public boolean canDivideBy(int x) {
            return x != 13;
        }
    }
}
