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
import com.loki2302.dto.BlogServiceErrorCode;
import com.loki2302.dto.PostDTO;
import com.loki2302.dto.ServiceResult;
import com.loki2302.dto.UserDTO;
import com.loki2302.service.BlogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class BlogServiceTest {

	@Autowired
	BlogService blogService;
	
	@Test
	public void sessionExpiresAfter3Seconds() throws InterruptedException {
		UserDTO user = createUser("loki2302", "qwerty");
		AuthenticationResultDTO authenticationResult = authenticate(
				"loki2302", "qwerty");
		
		String sessionToken = authenticationResult.SessionToken;
		createPost(sessionToken, "test1");
		Thread.sleep(3500);		
		
		ServiceResult<PostDTO> createPostResult = blogService.createPost(
				sessionToken, 
				"test2");
		assertFalse(createPostResult.ok);
		assertNull(createPostResult.payload);		
		assertEquals(
				BlogServiceErrorCode.SessionExpired, 
				createPostResult.blogServiceErrorCode);
		
		createPostResult = blogService.createPost(
				sessionToken, 
				"test3");
		assertFalse(createPostResult.ok);
		assertNull(createPostResult.payload);		
		assertEquals(
				BlogServiceErrorCode.NoSuchSession, 
				createPostResult.blogServiceErrorCode);
	}
		
	@Test
	public void canCreatePost() {
		UserDTO user = createUser("loki2302", "qwerty");
		AuthenticationResultDTO authenticationResult = authenticate(
				"loki2302", "qwerty");
		
		String sessionToken = authenticationResult.SessionToken;
		
		ServiceResult<PostDTO> createPostResult = blogService.createPost(
				sessionToken, 
				"text goes here");
		assertTrue(createPostResult.ok);
		assertNotNull(createPostResult.payload);
		assertTrue(createPostResult.payload.PostId > 0);
		assertEquals("text goes here", createPostResult.payload.Text);
		assertEquals("loki2302", createPostResult.payload.UserName);
		assertEquals(user.UserId, createPostResult.payload.UserId);
	}
	
	@Test
	public void cantCreatePostIfTextIsTooLong() {
		UserDTO user = createUser("loki2302", "qwerty");
		AuthenticationResultDTO authenticationResult = authenticate(
				"loki2302", "qwerty");
		
		String sessionToken = authenticationResult.SessionToken;
		
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < 1025; ++i) {
			stringBuilder.append('a');
		}
		
		ServiceResult<PostDTO> createPostResult = blogService.createPost(
				sessionToken, 
				stringBuilder.toString());
		assertFalse(createPostResult.ok);
		assertNull(createPostResult.payload);		
		assertEquals(
				BlogServiceErrorCode.ValidationError, 
				createPostResult.blogServiceErrorCode);
		assertTrue(createPostResult.fieldErrors.containsKey("text"));
	}
	
	@Test
	public void canUpdatePost() {
		UserDTO user = createUser("loki2302", "qwerty");
		AuthenticationResultDTO authenticationResult = authenticate(
				"loki2302", "qwerty");
		
		String sessionToken = authenticationResult.SessionToken;
		
		PostDTO post = createPost(sessionToken, "text goes here");
		ServiceResult<PostDTO> updatePostResult = blogService.updatePost(
				sessionToken, 
				post.PostId, 
				"new text goes here");
		assertTrue(updatePostResult.ok);
		assertNotNull(updatePostResult.payload);
		assertEquals(post.PostId, updatePostResult.payload.PostId);
		assertEquals(post.UserId, updatePostResult.payload.UserId);
		assertEquals(post.UserName, updatePostResult.payload.UserName);
		assertEquals("new text goes here", updatePostResult.payload.Text);
	}
	
	@Test
	public void cantUpdatePostThatDoesNotExist() {
		// TODO
	}
	
	@Test
	public void cantUpdatePostThatDoesNotBelongToTheUser() {
		// TODO
	}	
	
	@Test
	public void cantUpdatePostIfTextIsTooLong() {
		// TODO
	}
	
	@Test
	public void canDeletePost() {
		// TODO
	}
	
	@Test
	public void cantDeletePostThatDoesNotExist() {
		// TODO
	}
	
	@Test
	public void cantDeletePostThatDoesnBelongToTheUser() {
		// TODO
	}
	
	private UserDTO createUser(
			String userName, 
			String password) {
		
		ServiceResult<UserDTO> result = blogService.createUser(
				userName, 
				password);
		
		assertTrue(result.ok);
		
		return result.payload;
	}
	
	private AuthenticationResultDTO authenticate(
			String userName, 
			String password) {
		
		ServiceResult<AuthenticationResultDTO> result = blogService.authenticate(
				userName, 
				password);
		
		assertTrue(result.ok);
		
		return result.payload;
	}
	
	private PostDTO createPost(
			String sessionToken, 
			String text) {
		
		ServiceResult<PostDTO> result = blogService.createPost(
				sessionToken, 
				text);
		
		assertTrue(result.ok);
		
		return result.payload;		
	}
	
	private PostDTO getPost(
			String sessionToken, 
			long postId) {
		
		ServiceResult<PostDTO> result = blogService.getPost(
				sessionToken, 
				postId);
		
		assertTrue(result.ok);
		
		return result.payload;
	}
	
	private PostDTO updatePost(
			String sessionToken, 
			long postId, 
			String text) {
		
		return null;
	}
	
	private void deletePost(
			String sessionToken, 
			long postId) {
		
		ServiceResult<Object> result = blogService.deletePost(
				sessionToken,
				postId);
		
		assertTrue(result.ok);
	}
	
}
