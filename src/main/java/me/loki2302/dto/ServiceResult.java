package me.loki2302.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceResult<T> {
	public T payload;
	public boolean ok;
	public BlogServiceErrorCode blogServiceErrorCode;
	public Map<String, List<String>> fieldErrors;
	
	public static <T> ServiceResult<T> ok(T payload) {
		ServiceResult<T> result = new ServiceResult<T>();
		result.payload = payload;
		result.ok = true;
		return result;
	}
	
	public static <T> ServiceResult<T> error(
			BlogServiceErrorCode blogServiceErrorCode,
			Map<String, List<String>> fieldErrors) {
		ServiceResult<T> result = new ServiceResult<T>();			
		result.ok = false;
		result.blogServiceErrorCode = blogServiceErrorCode;
		result.fieldErrors = fieldErrors;
		return result;
	}
	
	public static <T> ServiceResult<T> error(
			BlogServiceErrorCode blogServiceErrorCode) {
		return error(blogServiceErrorCode, new HashMap<String, List<String>>());
	}
}