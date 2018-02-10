package me.loki2302.embeddables;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
public class EmbeddablesTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional // fails without this
    public void canSaveAndRetrieveAnEntityWithEmbeddables() {
        User user = user(profile("loki2302", 40),
                interest("programming"),
                interest("coffee"));
        user = userRepository.save(user);
        long userId = user.id;

        User retrievedUser = userRepository.findOne(userId);
        assertEquals("loki2302", retrievedUser.profile.name);
        assertEquals(40, retrievedUser.profile.age);
        assertEquals(2, retrievedUser.interests.size());
    }

    private static User user(Profile profile, Interest... interests) {
        User user = new User();
        user.profile = profile;
        user.interests = Arrays.asList(interests);
        return user;
    }

    private static Profile profile(String name, int age) {
        Profile profile = new Profile();
        profile.name = name;
        profile.age = age;
        return profile;
    }

    private static Interest interest(String name) {
        Interest interest = new Interest();
        interest.name = name;
        return interest;
    }
}
