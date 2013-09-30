package me.loki2302.service.transactionscripts;

import me.loki2302.dto.AuthenticationResultDTO;
import me.loki2302.dto.UserDTO;
import me.loki2302.entities.Session;
import me.loki2302.entities.XUser;
import me.loki2302.service.implementation.AuthenticationManager;
import me.loki2302.service.implementation.BlogServiceException;
import me.loki2302.service.implementation.UserAndPostCount;
import me.loki2302.service.implementation.UserDetailsRetriever;
import me.loki2302.service.validation.ThrowingValidator;
import me.loki2302.service.validation.subjects.UserNameAndPasswordSubject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthenticateTransactionScript {
	@Autowired ThrowingValidator throwingValidator;
	@Autowired AuthenticationManager authenticationManager;	
	@Autowired UserDetailsRetriever userDetailsRetriever;
	
	public AuthenticationResultDTO authenticate(
			String userName, 
			String password) throws BlogServiceException {
		
		UserNameAndPasswordSubject userNameAndPasswordSubject = 
				new UserNameAndPasswordSubject();
		userNameAndPasswordSubject.userName = userName;
		userNameAndPasswordSubject.password = password;
		throwingValidator.Validate(userNameAndPasswordSubject);
		
		Session session = authenticationManager.authenticate(
				userName, 
				password);
		
		XUser user = session.getUser();
		
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