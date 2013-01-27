package com.loki2302.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.AuthenticationResultDTO;
import com.loki2302.dto.PostDTO;
import com.loki2302.dto.UserDTO;
import com.loki2302.entities.Post;
import com.loki2302.entities.Session;
import com.loki2302.entities.User;
import com.loki2302.repositories.PostRepository;
import com.loki2302.repositories.UserRepository;

@Service
public class BlogService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;	
	
	@Autowired
	AuthenticationManager authenticationManager;
	
	@Autowired
	PostMapper postMapper;
	
	public UserDTO createUser(String userName, String password) {
		User user = new User();
		user.setUserName(userName);
		user.setPassword(password);
		user = userRepository.save(user);
		
		UserDTO userDto = new UserDTO();
		userDto.UserId = user.getId();
		userDto.UserName = user.getUserName();
		userDto.NumberOfPosts = postRepository.countByAuthor(user);
		
		return userDto;
	}
	
	public AuthenticationResultDTO authenticate(String userName, String password) {
		Session session = authenticationManager.authenticate(userName, password);
		
		AuthenticationResultDTO authenticationResultDto = new AuthenticationResultDTO();
		authenticationResultDto.SessionToken = session.getSessionToken();
		
		return authenticationResultDto;
	}
	
	public PostDTO createPost(String sessionToken, String text) {
		User user = authenticationManager.getUser(sessionToken);		
		Post post = new Post();
		post.setAuthor(user);
		post.setText(text);
		post = postRepository.save(post);
		return postMapper.build(post);
	}
	
	public PostDTO getPost(String sessionToken, long postId) {
		User user = authenticationManager.getUser(sessionToken);
		Post post = postRepository.findOne(postId);
		if(!post.getAuthor().equals(user)) {
			throw new RuntimeException("no permissions to access the post");
		}
		
		return postMapper.build(post);		
	}
	
}
