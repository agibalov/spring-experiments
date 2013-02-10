package com.loki2302.service.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loki2302.service.implementation.BlogServiceValidationException;

@Service
public class ThrowingValidator {
	@Autowired Validator validator;
	
	public void Validate(Object o) throws BlogServiceValidationException {
		Set<ConstraintViolation<Object>> violations = 
				validator.validate(o);
		if(violations.size() == 0) {
			return;
		}
		
		Map<String, List<String>> fields = new HashMap<String, List<String>>();
		for(ConstraintViolation<Object> violation : violations) {
			String fieldName = violation.getPropertyPath().toString();
			List<String> fieldErrors = fields.get(fieldName);
			if(fieldErrors == null) {
				fieldErrors = new ArrayList<String>();
				fields.put(fieldName, fieldErrors);
			}
			
			String message = violation.getMessage();
			fieldErrors.add(message);
		}
			
		throw new BlogServiceValidationException(fields);			
	}
}