package me.loki2302.service.implementation;

import java.util.Date;
import java.util.UUID;

import me.loki2302.dto.BlogServiceErrorCode;
import me.loki2302.entities.Session;
import me.loki2302.entities.XUser;
import me.loki2302.repositories.SessionRepository;
import me.loki2302.repositories.UserRepository;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthenticationManager {
	@Autowired UserRepository userRepository;	
	@Autowired SessionRepository sessionRepository;
	int sessionPeriod = 3; // TODO: make configurable
		
	public Session authenticate(
			String userName, 
			String password) throws BlogServiceException {
		
		XUser user = userRepository.findUserByName(userName);
		if(user == null) {
			throw new BlogServiceException(BlogServiceErrorCode.BadUserNameOrPassword);
		}
		
		if(!user.getPassword().equals(password)) {
			throw new BlogServiceException(BlogServiceErrorCode.BadUserNameOrPassword);
		}
		
		Session session = new Session();
		session.setUser(user);
		session.setSessionToken(UUID.randomUUID().toString());
		session.setLastActivity(new Date());
		session = sessionRepository.save(session);
		
		return session;			
	}
	
	public XUser getUser(String sessionToken) throws BlogServiceException {
		Session session = sessionRepository.findBySessionToken(sessionToken);
		if(session == null) {
			throw new BlogServiceException(BlogServiceErrorCode.NoSuchSession);
		}
		
		DateTime lastActivity = new DateTime(session.getLastActivity());
		DateTime currentTime = new DateTime(new Date());
		Seconds sessionSeconds = Seconds.secondsBetween(lastActivity, currentTime);		
		if(sessionSeconds.getSeconds() >= sessionPeriod) {
			sessionRepository.delete(session);			
			throw new BlogServiceException(BlogServiceErrorCode.SessionExpired); 
		}
		
		session.setLastActivity(currentTime.toDate());
		session = sessionRepository.save(session);
		
		XUser user = session.getUser();
		return user;
	}
}