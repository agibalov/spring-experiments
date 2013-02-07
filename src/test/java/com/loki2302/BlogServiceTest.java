package com.loki2302;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.loki2302.dto.AuthenticationResultDTO;
import com.loki2302.dto.PostDTO;
import com.loki2302.dto.UserDTO;
import com.loki2302.service.BlogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:repository-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class BlogServiceTest {

	@Autowired
	BlogService blogService;
	
	@Test
	public void bloggingScenario() {
		UserDTO user = blogService.createUser("loki2302", "qwerty");
		assertEquals("loki2302", user.UserName);
		assertEquals(0, user.NumberOfPosts);
		
		AuthenticationResultDTO authenticationResult = blogService.authenticate("loki2302", "qwerty");
		assertNotNull(authenticationResult.SessionToken);
		
		String sessionToken = authenticationResult.SessionToken;
		
		PostDTO post = blogService.createPost(sessionToken, "hi there");
		assertEquals("hi there", post.Text);
		assertEquals("loki2302", post.UserName);
		assertEquals(user.UserId, post.UserId);
	}
	
}
