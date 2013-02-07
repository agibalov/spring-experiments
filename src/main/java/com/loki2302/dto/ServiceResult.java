package com.loki2302.dto;


public class ServiceResult<T> {
	public T payload;
	public boolean ok;
	public BlogServiceErrorCode blogServiceErrorCode;
	
	public static <T> ServiceResult<T> ok(T payload) {
		ServiceResult<T> result = new ServiceResult<T>();
		result.payload = payload;
		result.ok = true;
		return result;
	}
	
	public static <T> ServiceResult<T> error(BlogServiceErrorCode blogServiceErrorCode) {
		ServiceResult<T> result = new ServiceResult<T>();			
		result.ok = false;
		result.blogServiceErrorCode = blogServiceErrorCode;
		return result;
	}
}