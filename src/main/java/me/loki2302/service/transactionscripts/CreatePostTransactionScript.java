package me.loki2302.service.transactionscripts;

import me.loki2302.dto.PostDTO;
import me.loki2302.entities.Post;
import me.loki2302.entities.XUser;
import me.loki2302.repositories.PostRepository;
import me.loki2302.service.implementation.AuthenticationManager;
import me.loki2302.service.implementation.BlogServiceException;
import me.loki2302.service.implementation.PostMapper;
import me.loki2302.service.validation.ThrowingValidator;
import me.loki2302.service.validation.subjects.PostSubject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CreatePostTransactionScript {
	@Autowired ThrowingValidator throwingValidator;
	@Autowired AuthenticationManager authenticationManager;	
	@Autowired PostRepository postRepository;	
	@Autowired PostMapper postMapper;
	
	public PostDTO createPost(
			String sessionToken, 
			String text) throws BlogServiceException {
		
		PostSubject postSubject = new PostSubject();
		postSubject.text = text;
		throwingValidator.Validate(postSubject);
		
		XUser user = authenticationManager.getUser(sessionToken);		
		Post post = new Post();
		post.setAuthor(user);
		post.setText(text);
		post = postRepository.save(post);
		
		return postMapper.build(post);
	}	
}