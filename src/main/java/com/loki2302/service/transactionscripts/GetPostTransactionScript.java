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

@Service
public class GetPostTransactionScript {
	@Autowired AuthenticationManager authenticationManager;	
	@Autowired PostRepository postRepository;	
	@Autowired PostMapper postMapper;
	
	public PostDTO getPost(String sessionToken, long postId) throws BlogServiceException {
		User user = authenticationManager.getUser(sessionToken);
		Post post = postRepository.findOne(postId);
		if(post == null) {
			throw new BlogServiceException(BlogServiceErrorCode.NoSuchPost);
		}
		
		if(!post.getAuthor().equals(user)) {
			throw new BlogServiceException(BlogServiceErrorCode.NoPermissionsToAccessPost);
		}
		
		return postMapper.build(post);
	}
}