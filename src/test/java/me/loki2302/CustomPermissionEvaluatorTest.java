package me.loki2302;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;

@RunWith(SpringJUnit4ClassRunner.class)
@IntegrationTest
@SpringApplicationConfiguration(classes = CustomPermissionEvaluatorTest.Config.class)
public class CustomPermissionEvaluatorTest {
    @Autowired
    private NoteService noteService;

    @Test
    public void canViewNote() {
        noteService.viewNote(new Note());
    }

    @Test(expected = AccessDeniedException.class)
    public void cantEditNote() {
        noteService.editNote(new Note());
    }

    @Configuration
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    public static class Config {
        @Bean
        AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) {
            return builder.getOrBuild();
        }

        @Bean(name = "expressionHandler")
        DefaultMethodSecurityExpressionHandler expressionHandler() {
            DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
            handler.setPermissionEvaluator(permissionEvaluator());
            return handler;
        }

        @Bean
        PermissionEvaluator permissionEvaluator() {
            return new MyPermissionEvaluator();
        }

        @Bean
        NoteService dummyService() {
            return new NoteService();
        }
    }

    public static class MyPermissionEvaluator implements PermissionEvaluator {
        @Override
        public boolean hasPermission(
                Authentication authentication,
                Object targetDomainObject,
                Object permission) {

            if(Note.class.equals(targetDomainObject.getClass())) {
                if(permission.equals("VIEW")) {
                    return true;
                } else if(permission.equals("EDIT")) {
                    return false;
                }
            }

            return false;
        }

        @Override
        public boolean hasPermission(
                Authentication authentication,
                Serializable targetId,
                String targetType,
                Object permission) {

            // TODO: put something here as well

            return false;
        }
    }

    public static class NoteService {
        @PreAuthorize("hasPermission(#note, 'VIEW')")
        public void viewNote(Note note) {
        }

        @PreAuthorize("hasPermission(#note, 'EDIT')")
        public void editNote(Note note) {
        }
    }

    public static class Note {
    }
}
