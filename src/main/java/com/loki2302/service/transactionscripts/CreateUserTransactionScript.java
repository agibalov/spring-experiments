package com.loki2302.service.transactionscripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.BlogServiceErrorCode;
import com.loki2302.dto.UserDTO;
import com.loki2302.entities.User;
import com.loki2302.repositories.UserRepository;
import com.loki2302.service.implementation.BlogServiceException;
import com.loki2302.service.implementation.UserAndPostCount;
import com.loki2302.service.implementation.UserDetailsRetriever;
import com.loki2302.service.validation.ThrowingValidator;
import com.loki2302.service.validation.subjects.UserNameAndPasswordSubject;

@Service
public class CreateUserTransactionScript {	
	@Autowired ThrowingValidator throwingValidator;
	@Autowired UserRepository userRepository;	
	@Autowired UserDetailsRetriever userDetailsRetriever;
	
	public UserDTO createUser(
			String userName, 
			String password) throws BlogServiceException {
		
		UserNameAndPasswordSubject userNameAndPasswordSubject = 
				new UserNameAndPasswordSubject();
		userNameAndPasswordSubject.userName = userName;
		userNameAndPasswordSubject.password = password;
		throwingValidator.Validate(userNameAndPasswordSubject);
		
		User user = userRepository.findUserByName(userName);
		if(user != null) {
			throw new BlogServiceException(BlogServiceErrorCode.UserAlreadyRegistered);
		}
		
		user = new User();
		user.setUserName(userName);
		user.setPassword(password);
		user = userRepository.save(user);
		
		UserAndPostCount result = userDetailsRetriever.getUserDetails(user.getId());		
		UserDTO userDto = new UserDTO();
		userDto.UserId = result.User.getId();
		userDto.UserName = result.User.getUserName();		
		userDto.NumberOfPosts = result.PostCount;
		
		return userDto;
	}
}