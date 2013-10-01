package me.loki2302.service.transactionscripts;

import me.loki2302.dto.BlogServiceErrorCode;
import me.loki2302.dto.UserDTO;
import me.loki2302.entities.User;
import me.loki2302.repositories.UserRepository;
import me.loki2302.service.implementation.BlogServiceException;
import me.loki2302.service.implementation.UserAndPostCount;
import me.loki2302.service.implementation.UserDetailsRetriever;
import me.loki2302.service.validation.ThrowingValidator;
import me.loki2302.service.validation.subjects.UserNameAndPasswordSubject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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