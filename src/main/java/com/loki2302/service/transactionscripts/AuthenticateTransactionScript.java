package com.loki2302.service.transactionscripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.AuthenticationResultDTO;
import com.loki2302.dto.UserDTO;
import com.loki2302.entities.Session;
import com.loki2302.entities.User;
import com.loki2302.service.implementation.AuthenticationManager;
import com.loki2302.service.implementation.BlogServiceException;
import com.loki2302.service.implementation.UserAndPostCount;
import com.loki2302.service.implementation.UserDetailsRetriever;
import com.loki2302.service.validation.ThrowingValidator;
import com.loki2302.service.validation.subjects.UserNameAndPassword;

@Service
public class AuthenticateTransactionScript {
	@Autowired ThrowingValidator throwingValidator;
	@Autowired AuthenticationManager authenticationManager;	
	@Autowired UserDetailsRetriever userDetailsRetriever;
	
	public AuthenticationResultDTO authenticate(
			String userName, 
			String password) throws BlogServiceException {
		
		UserNameAndPassword userNameAndPassword = new UserNameAndPassword();
		userNameAndPassword.userName = userName;
		userNameAndPassword.password = password;
		throwingValidator.Validate(userNameAndPassword);
		
		Session session = authenticationManager.authenticate(
				userName, 
				password);
		
		User user = session.getUser();
		
		UserAndPostCount result = userDetailsRetriever.getUserDetails(
				user.getId());		
				
		AuthenticationResultDTO authenticationResultDto = new AuthenticationResultDTO();
		authenticationResultDto.SessionToken = session.getSessionToken();
		
		UserDTO userDto = new UserDTO();
		userDto.UserId = result.User.getId();
		userDto.UserName = result.User.getUserName();		
		userDto.NumberOfPosts = result.PostCount;
		authenticationResultDto.User = userDto;
		
		return authenticationResultDto;
	}	
}