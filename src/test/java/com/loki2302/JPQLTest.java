package com.loki2302;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.loki2302.entities.Post;
import com.loki2302.entities.User;
import com.loki2302.repositories.PostRepository;
import com.loki2302.repositories.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:repository-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class JPQLTest {
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Test
	public void canGetUserNamesAndPostCount() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		Query query = entityManager.createQuery(
	            "select u.userName, count(p.id) from User u " + 
	            "left join u.posts p group by u.userName");
	    List<Object> resultList = query.getResultList();
	    
	    assertEquals("loki2302", (String)((Object[])resultList.get(0))[0]);
	    assertEquals(7L, ((Long)((Object[])resultList.get(0))[1]).longValue());
	    
	    assertEquals("qwerty", (String)((Object[])resultList.get(1))[0]);
	    assertEquals(3L, ((Long)((Object[])resultList.get(1))[1]).longValue());
	    
	    assertEquals("lena", (String)((Object[])resultList.get(2))[0]);
	    assertEquals(12L, ((Long)((Object[])resultList.get(2))[1]).longValue());
	}
	
	private void createUserWithPosts(String userName, int numberOfPosts) {
		User user = new User();
		user.setUserName(userName);
		user.setPassword("qwerty");
		user = userRepository.save(user);
		
		for(int i = 0; i < numberOfPosts; ++i) {
			Post post = new Post();
			post.setText(String.format("Post #%d of %s", i, userName));
			post.setText("content here");
			post.setAuthor(user);
			post = postRepository.save(post);
		}
	}
}
