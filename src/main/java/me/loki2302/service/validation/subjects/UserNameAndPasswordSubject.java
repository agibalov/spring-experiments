package me.loki2302.service.validation.subjects;

import me.loki2302.service.validation.Password;
import me.loki2302.service.validation.UserName;

public class UserNameAndPasswordSubject {
	@UserName
	public String userName;
	
	@Password
	public String password;
}