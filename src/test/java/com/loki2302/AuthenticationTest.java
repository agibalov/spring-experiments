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
import com.loki2302.dto.ServiceResult;
import com.loki2302.dto.UserDTO;
import com.loki2302.service.BlogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:applicationContext.xml", 
		"classpath:repository-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class AuthenticationTest {

	@Autowired
	BlogService blogService;	
	
	@Test
	public void cantCreateUserWithBadUserName() {
		ServiceResult<UserDTO> createUserResult = blogService.createUser(
				"",
				"qwerty");
		assertFalse(createUserResult.ok);
		assertEquals(
				BlogServiceErrorCode.ValidationError, 
				createUserResult.blogServiceErrorCode);
		assertTrue(createUserResult.fieldErrors.containsKey("userName"));
	}
	
	@Test
	public void cantCreateUserWithBadPassword() {
		ServiceResult<UserDTO> createUserResult = blogService.createUser(
				"loki2302",
				"");
		assertFalse(createUserResult.ok);
		assertEquals(
				BlogServiceErrorCode.ValidationError, 
				createUserResult.blogServiceErrorCode);
		assertTrue(createUserResult.fieldErrors.containsKey("password"));
	}
	
	@Test
	public void canRegister() {
		ServiceResult<UserDTO> createUserResult = blogService.createUser(
				"loki2302", 
				"qwerty");
		assertTrue(createUserResult.ok);
		assertNotNull(createUserResult.payload);
		assertEquals("loki2302", createUserResult.payload.UserName);
		assertTrue(createUserResult.payload.UserId > 0);
		assertEquals(0, createUserResult.payload.NumberOfPosts);
	}
	
	@Test
	public void cantRegisterMoreThanOnce() {
		ServiceResult<UserDTO> createUserResult1 = blogService.createUser(
				"loki2302", 
				"qwerty");
		assertTrue(createUserResult1.ok);		
		
		ServiceResult<UserDTO> createUserResult2 = blogService.createUser(
				"loki2302", 
				"qwerty");
		assertFalse(createUserResult2.ok);	
		assertEquals(
				BlogServiceErrorCode.UserAlreadyRegistered, 
				createUserResult2.blogServiceErrorCode);
	}
	
	@Test
	public void canAuthenticateIfRegistered() {
		ServiceResult<UserDTO> createUserResult1 = blogService.createUser(
				"loki2302", 
				"qwerty");
		assertTrue(createUserResult1.ok);
		
		ServiceResult<AuthenticationResultDTO> authenticationResult = 
				blogService.authenticate(
						"loki2302", 
						"qwerty");
		assertTrue(authenticationResult.ok);		
	}
	
	@Test
	public void cantAuthenticateIfNotRegistered() {
		ServiceResult<AuthenticationResultDTO> authenticationResult = 
				blogService.authenticate(
						"loki2302", 
						"qwerty");
		assertFalse(authenticationResult.ok);
		assertEquals(
				BlogServiceErrorCode.BadUserNameOrPassword, 
				authenticationResult.blogServiceErrorCode);
	}
	
	@Test
	public void cantAuthenticateWithWrongPassword() {
		// TODO
	}
	
}
