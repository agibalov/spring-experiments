package me.loki2302.service.implementation;

import me.loki2302.entities.XUser;

public class UserAndPostCount {
	public XUser User;
	public long PostCount;
	
	public UserAndPostCount(XUser user, long postCount) {
		this.User = user;
		this.PostCount = postCount;
	}
}