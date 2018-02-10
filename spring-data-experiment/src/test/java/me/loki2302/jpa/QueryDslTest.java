package me.loki2302.jpa;

import static org.junit.Assert.*;

import java.util.List;

import me.loki2302.jpa.entities.QUser;
import me.loki2302.jpa.entities.User;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.mysema.query.jpa.impl.JPAQuery;

public abstract class QueryDslTest extends AbstractSpringDataJPATest {
    @Test
    public void canFindAllInRepositoryByPredicate() {
        User loki = userRepository.save(user("loki"));
        userRepository.save(user("jsmith"));
        
        QUser qUser = QUser.user;
        List<User> users = Lists.newArrayList(userRepository.findAll(qUser.userName.eq("loki")));
        assertEquals(1, users.size());
        assertEquals(loki.getId(), users.get(0).getId());
    }
    
    @Test
    public void canFinalAllInEntityManagerByQuery() {
        User loki = userRepository.save(user("loki"));
        userRepository.save(user("jsmith"));
        
        QUser qUser = QUser.user;
        List<User> users = new JPAQuery(entityManager)
            .from(qUser)
            .where(qUser.userName.eq("loki"))
            .list(qUser);
        assertEquals(1, users.size());
        assertEquals(loki.getId(), users.get(0).getId());
    }

	private static User user(String userName) {
	    User user = new User();
	    user.setUserName(userName);
	    return user;
	}
}