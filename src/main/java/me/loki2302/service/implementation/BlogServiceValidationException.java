package me.loki2302.service.implementation;

import java.util.List;
import java.util.Map;

import me.loki2302.dto.BlogServiceErrorCode;

public class BlogServiceValidationException extends BlogServiceException {
	private static final long serialVersionUID = -2170336409432284783L;
	
	private final Map<String, List<String>> fieldErrors;
	
	public BlogServiceValidationException(Map<String, List<String>> fieldErrors) {
		super(BlogServiceErrorCode.ValidationError);
		this.fieldErrors = fieldErrors;
	}
	
	public Map<String, List<String>> getFieldErrors() {
		return fieldErrors;
	}
}