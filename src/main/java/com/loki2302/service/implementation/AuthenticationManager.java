package com.loki2302.service.implementation;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.BlogServiceErrorCode;
import com.loki2302.entities.Session;
import com.loki2302.entities.User;
import com.loki2302.repositories.SessionRepository;
import com.loki2302.repositories.UserRepository;

@Service
public class AuthenticationManager {
	@Autowired UserRepository userRepository;	
	@Autowired SessionRepository sessionRepository;
	
	public Session authenticate(
			String userName, 
			String password) throws BlogServiceException {
		
		User user = userRepository.findUserByName(userName);
		if(user == null) {
			throw new BlogServiceException(BlogServiceErrorCode.BadUserNameOrPassword);
		}
		
		if(!user.getPassword().equals(password)) {
			throw new BlogServiceException(BlogServiceErrorCode.BadUserNameOrPassword);
		}
		
		Session session = new Session();
		session.setUser(user);
		session.setSessionToken(UUID.randomUUID().toString());
		session = sessionRepository.save(session);
		
		return session;			
	}
	
	public User getUser(String sessionToken) {
		Session session = sessionRepository.findBySessionToken(sessionToken);
		if(session == null) {
			throw new RuntimeException("No such session");
		}
		
		User user = session.getUser();
		return user;
	}
}