package com.loki2302.service.validation.subjects;

import com.loki2302.service.validation.Password;
import com.loki2302.service.validation.UserName;

public class UserNameAndPasswordSubject {
	@UserName
	public String userName;
	
	@Password
	public String password;
}