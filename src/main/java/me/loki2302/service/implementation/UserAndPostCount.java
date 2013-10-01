package me.loki2302.service.implementation;

import me.loki2302.entities.User;

public class UserAndPostCount {
	public User User;
	public long PostCount;
	
	public UserAndPostCount(User user, long postCount) {
		this.User = user;
		this.PostCount = postCount;
	}
}