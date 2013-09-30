package me.loki2302.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Session {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true)
	private String sessionToken;
	
	@ManyToOne
	private XUser user;
	
	private Date lastActivity;
	
	public Long getId() {
		return id;
	}
	
	public String getSessionToken() {
		return sessionToken;
	}
	
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
	}
	
	public XUser getUser() {
		return user;
	}
	
	public void setUser(XUser user) {
		this.user = user;
	}
	
	public Date getLastActivity() {
		return lastActivity;
	}
	
	public void setLastActivity(Date lastActivity) {
		this.lastActivity = lastActivity;
	}
}