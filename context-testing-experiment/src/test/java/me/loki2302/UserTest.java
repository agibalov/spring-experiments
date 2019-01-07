package me.loki2302;

import me.loki2302.impl.AppConfig;
import me.loki2302.impl.BlogService;
import me.loki2302.impl.UserAlreadyExistsException;
import me.loki2302.impl.UserNotFoundException;
import me.loki2302.impl.entities.User;
import me.loki2302.impl.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UserTest {
    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @IntegrationTest
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = AppConfig.class)
    public static class GivenThereIsNothing {
        @Autowired
        private BlogService blogService;

        @Test
        public void canCreateUser() {
            User user = blogService.createUser("loki2302");
            assertNotNull(user.id);
        }

        @Test(expected = UserNotFoundException.class)
        public void cantGetUserByIdIfUserDoesNotExist() {
            blogService.getUser(123);
        }

        @Test(expected = UserNotFoundException.class)
        public void cantGetUserByNameIfUserDoesNotExist() {
            blogService.getUser("loki2302");
        }

        @Test(expected = UserNotFoundException.class)
        public void cantUpdateUserIfUserDoesNotExist() {
            blogService.updateUser(123, "andrey");
        }

        @Test(expected = UserNotFoundException.class)
        public void cantDeleteUserIfUserDoesNotExist() {
            blogService.deleteUser(123);
        }
    }

    @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
    @IntegrationTest
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringApplicationConfiguration(classes = {AppConfig.class, OneUserTestConfig.class})
    public static class GivenThereIsAUser {
        @Autowired
        private User predefinedUser;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private BlogService blogService;

        @Test(expected = UserAlreadyExistsException.class)
        public void cantCreateUserWhenUserAlreadyExists() {
            blogService.createUser(predefinedUser.name);
        }

        @Test
        public void canUpdateUser() {
            User updatedUser = blogService.updateUser(predefinedUser.id, "andrey");
            assertEquals(predefinedUser.id, updatedUser.id);
            assertEquals("andrey", updatedUser.name);
        }

        @Test
        public void canGetAnExistingUserById() {
            User retrievedUser = blogService.getUser(predefinedUser.id);
            assertEquals(predefinedUser.id, retrievedUser.id);
        }

        @Test
        public void canGetAnExistingUserByName() {
            User retrievedUser = blogService.getUser(predefinedUser.name);
            assertEquals(predefinedUser.id, retrievedUser.id);
        }

        @Test
        public void canDeleteUser() {
            blogService.deleteUser(predefinedUser.id);
            assertEquals(0, userRepository.count());
        }
    }
}
