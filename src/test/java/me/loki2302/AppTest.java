package me.loki2302;
import static org.junit.Assert.*;

import java.util.List;

import me.loki2302.MyConfiguration;
import me.loki2302.entities.Post;
import me.loki2302.entities.XUser;
import me.loki2302.repositories.PostRepository;
import me.loki2302.repositories.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MyConfiguration.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class AppTest {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;
				
	@Test
	public void thereAreNoUsersByDefault() {
		long count = userRepository.count();
		assertEquals(0, count);
	}
	
	@Test
	public void canCreateUser() {
		XUser user = new XUser();
		user.setUserName("loki2302");
		user.setPassword("qwerty");
		
		assertNull(user.getId());
		user = userRepository.save(user);
		assertNotNull(user.getId());
		assertEquals(1, userRepository.count());
		
		assertEquals("loki2302", user.getUserName());
		assertEquals("qwerty", user.getPassword());
		
		XUser retrievedUser = userRepository.findOne(user.getId());
		assertEquals("loki2302", retrievedUser.getUserName());
		assertEquals("qwerty", retrievedUser.getPassword());
		
		retrievedUser = userRepository.findUserByName("loki2302");
		assertEquals("loki2302", retrievedUser.getUserName());
		assertEquals("qwerty", retrievedUser.getPassword());
		
		retrievedUser.setUserName("2302loki");
		retrievedUser = userRepository.findOne(user.getId());
		assertEquals("2302loki", retrievedUser.getUserName());
		
		userRepository.delete(retrievedUser.getId());
		assertEquals(0, userRepository.count());
	}
	
	@Test
	public void cantRetrieveUserForBadId() {
		XUser user = userRepository.findOne(123L);
		assertNull(user);
	}
	
	@Test
	public void cantRetrieveUserForBadUserName() {
		XUser user = userRepository.findUserByName("qwerty");
		assertNull(user);
	}
	
	@Test
	public void canPageUsers() {
		int totalUsers = 100;
		
		for(int i = 0; i < totalUsers; ++i) {
			XUser user = new XUser();
			user.setUserName(String.format("user#%d", i + 1));
			userRepository.save(user);
		}
		assertEquals(100, userRepository.count());
		
		int pageNumber = 0;
		Page<XUser> page = null;
		do {
			PageRequest pageRequest = new PageRequest(pageNumber, 3);
			page = userRepository.findAll(pageRequest);
			assertEquals(pageNumber, page.getNumber());
			assertTrue(page.getNumberOfElements() <= 3);
			assertEquals(100, page.getTotalElements());
			
			++pageNumber;
		} while(page.hasNextPage());
		
		assertEquals(33, page.getNumber());
	}
	
	@Test
	public void canCreateUserAndPost() {
		XUser user = new XUser();
		user.setUserName("loki2302");
		user.setPassword("qwerty");
		user = userRepository.save(user);
		
		Post post = new Post();
		post.setAuthor(user);
		post.setText("post #1");
		post = postRepository.save(post);
		
		List<Post> userPosts = postRepository.findAllByAuthor(user);
		assertEquals(1, userPosts.size());
		
		long userPostsCount = postRepository.countByAuthor(user);
		assertEquals(1, userPostsCount);
	}
}
