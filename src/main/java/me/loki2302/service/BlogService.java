package me.loki2302.service;

import me.loki2302.dto.AuthenticationResultDTO;
import me.loki2302.dto.BlogServiceErrorCode;
import me.loki2302.dto.PostDTO;
import me.loki2302.dto.ServiceResult;
import me.loki2302.dto.UserDTO;
import me.loki2302.service.implementation.BlogServiceException;
import me.loki2302.service.implementation.BlogServiceValidationException;
import me.loki2302.service.implementation.UserDetailsRetriever;
import me.loki2302.service.transactionscripts.AuthenticateTransactionScript;
import me.loki2302.service.transactionscripts.CreatePostTransactionScript;
import me.loki2302.service.transactionscripts.CreateUserTransactionScript;
import me.loki2302.service.transactionscripts.DeletePostTransactionScript;
import me.loki2302.service.transactionscripts.GetPostTransactionScript;
import me.loki2302.service.transactionscripts.UpdatePostTransactionScript;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class BlogService {		
	@Autowired CreateUserTransactionScript createUserTransactionScript;	
	@Autowired AuthenticateTransactionScript authenticateTransactionScript;	
	@Autowired UserDetailsRetriever userDetailsRetriever;
	@Autowired CreatePostTransactionScript createPostTransactionScript;	
	@Autowired GetPostTransactionScript getPostTransactionScript;	
	@Autowired DeletePostTransactionScript deletePostTransactionScript;	
	@Autowired UpdatePostTransactionScript updatePostTransactionScript;
	
	public ServiceResult<UserDTO> createUser(
			final String userName, 
			final String password) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<UserDTO>() {
			@Override 
			public UserDTO execute() throws BlogServiceException {
				return createUserTransactionScript.createUser(
						userName, 
						password);
			}			
		});
	}
	
	public ServiceResult<AuthenticationResultDTO> authenticate(
			final String userName, 
			final String password) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<AuthenticationResultDTO>() {
			@Override 
			public AuthenticationResultDTO execute() throws BlogServiceException {
				return authenticateTransactionScript.authenticate(
						userName, 
						password);
			}			
		});
	}
	
	public ServiceResult<PostDTO> createPost(
			final String sessionToken, 
			final String text) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<PostDTO>() {
			@Override
			public PostDTO execute() throws BlogServiceException {
				return createPostTransactionScript.createPost(
						sessionToken, 
						text);
			}			
		});		
	}
	
	public ServiceResult<PostDTO> getPost(
			final String sessionToken, 
			final long postId) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<PostDTO>() {
			@Override 
			public PostDTO execute() throws BlogServiceException {
				return getPostTransactionScript.getPost(
						sessionToken, 
						postId);
			}			
		});		
	}
	
	public ServiceResult<Object> deletePost(
			final String sessionToken, 
			final long postId) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<Object>() {
			@Override 
			public Object execute() throws BlogServiceException {
				deletePostTransactionScript.deletePost(
						sessionToken, 
						postId);
				
				return null;
			}			
		});
	}
	
	public ServiceResult<PostDTO> updatePost(
			final String sessionToken, 
			final long postId, 
			final String text) {
		
		return ExecuteWithExceptionHandling(new ServiceAction<PostDTO>() {			
			@Override 
			public PostDTO execute() throws BlogServiceException {				
				return updatePostTransactionScript.updatePost(
						sessionToken, 
						postId, 
						text);
			}			
		});
	}
	
	private static <T> ServiceResult<T> ExecuteWithExceptionHandling(ServiceAction<T> function) {
		try {
			T result = function.execute();			
			return ServiceResult.ok(result);
		} catch(BlogServiceValidationException e) {
			return ServiceResult.error(
					e.getBlogServiceErrorCode(),
					e.getFieldErrors()); 
		} catch(BlogServiceException e) {
			return ServiceResult.error(e.getBlogServiceErrorCode());
		} catch(RuntimeException e) {
			return ServiceResult.error(BlogServiceErrorCode.InternalError);
		}
	}
	
	public static interface ServiceAction<T> {
		T execute() throws BlogServiceException;
	}
}
