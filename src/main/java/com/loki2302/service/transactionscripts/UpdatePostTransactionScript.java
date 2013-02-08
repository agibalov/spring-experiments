package com.loki2302.service.transactionscripts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.dto.BlogServiceErrorCode;
import com.loki2302.dto.PostDTO;
import com.loki2302.entities.Post;
import com.loki2302.entities.User;
import com.loki2302.repositories.PostRepository;
import com.loki2302.service.implementation.AuthenticationManager;
import com.loki2302.service.implementation.BlogServiceException;
import com.loki2302.service.implementation.PostMapper;
import com.loki2302.service.validation.ThrowingValidator;
import com.loki2302.service.validation.subjects.PostSubject;

@Service
public class UpdatePostTransactionScript {
	@Autowired ThrowingValidator throwingValidator;
	@Autowired AuthenticationManager authenticationManager;	
	@Autowired PostRepository postRepository;	
	@Autowired PostMapper postMapper;
	
	public PostDTO updatePost(
			String sessionToken, 
			long postId, 
			String text) throws BlogServiceException {
		
		PostSubject postSubject = new PostSubject();
		postSubject.text = text;
		throwingValidator.Validate(postSubject);
		
		User user = authenticationManager.getUser(sessionToken);
		Post post = postRepository.findOne(postId);
		if(post == null) {
			throw new BlogServiceException(BlogServiceErrorCode.NoSuchPost);
		}
		
		if(!post.getAuthor().equals(user)) {
			throw new BlogServiceException(BlogServiceErrorCode.NoPermissionsToAccessPost);
		}
		
		post.setText(text);
		post = postRepository.save(post);
		
		return postMapper.build(post);
	}	
}