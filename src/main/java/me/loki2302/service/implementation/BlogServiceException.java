package me.loki2302.service.implementation;

import me.loki2302.dto.BlogServiceErrorCode;

public class BlogServiceException extends Exception {
	private static final long serialVersionUID = -6064575307294795999L;
	
	private final BlogServiceErrorCode blogServiceErrorCode;
	
	public BlogServiceException(BlogServiceErrorCode blogServiceErrorCode) {
		this.blogServiceErrorCode = blogServiceErrorCode;
	}
	
	public BlogServiceErrorCode getBlogServiceErrorCode() {
		return blogServiceErrorCode;
	}
}