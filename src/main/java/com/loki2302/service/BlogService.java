package com.loki2302.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

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
	
	@PersistenceContext
	EntityManager entityManager;
	
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
		
		UserAndPostCount result = getUserDetails(user.getId());		
		UserDTO userDto = new UserDTO();
		userDto.UserId = result.User.getId();
		userDto.UserName = result.User.getUserName();		
		userDto.NumberOfPosts = result.PostCount;
		
		return userDto;
	}
	
	private UserAndPostCount getUserDetails(long userId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserAndPostCount> criteriaQuery = 
				criteriaBuilder.createQuery(UserAndPostCount.class);
		Root<User> root = criteriaQuery.from(User.class);
		Join<User, Post> postsExpression = root.join("posts", JoinType.LEFT);
		Expression<Long> postCountExpression = criteriaBuilder.count(postsExpression);
		criteriaQuery.multiselect(root, postCountExpression);
		criteriaQuery.groupBy(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("id"), userId));		
		TypedQuery<UserAndPostCount> typedQuery = entityManager.createQuery(criteriaQuery);
		UserAndPostCount result = typedQuery.getSingleResult();
		return result;
	}
	
	public AuthenticationResultDTO authenticate(String userName, String password) {
		Session session = authenticationManager.authenticate(userName, password);
		
		UserAndPostCount result = getUserDetails(session.getUser().getId());		
				
		AuthenticationResultDTO authenticationResultDto = new AuthenticationResultDTO();
		authenticationResultDto.SessionToken = session.getSessionToken();
		
		UserDTO userDto = new UserDTO();
		userDto.UserId = result.User.getId();
		userDto.UserName = result.User.getUserName();		
		userDto.NumberOfPosts = result.PostCount;
		authenticationResultDto.User = userDto;
		
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
