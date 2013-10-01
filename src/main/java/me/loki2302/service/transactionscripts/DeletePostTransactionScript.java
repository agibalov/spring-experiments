package me.loki2302.service.transactionscripts;

import me.loki2302.dto.BlogServiceErrorCode;
import me.loki2302.entities.Post;
import me.loki2302.entities.User;
import me.loki2302.repositories.PostRepository;
import me.loki2302.service.implementation.AuthenticationManager;
import me.loki2302.service.implementation.BlogServiceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class DeletePostTransactionScript {
	@Autowired AuthenticationManager authenticationManager;	
	@Autowired PostRepository postRepository;
	
	public void deletePost(
			String sessionToken, 
			long postId) throws BlogServiceException {
		
		User user = authenticationManager.getUser(sessionToken);
		Post post = postRepository.findOne(postId);
		if(post == null) {
			throw new BlogServiceException(BlogServiceErrorCode.NoSuchPost);
		}
		
		if(!post.getAuthor().equals(user)) {
			throw new BlogServiceException(BlogServiceErrorCode.NoPermissionsToAccessPost);
		}
		
		postRepository.delete(post);
	}	
}