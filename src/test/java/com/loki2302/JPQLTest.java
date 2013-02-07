package com.loki2302;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

public class JPQLTest extends AbstractSpringDataJPATest {	
	@Test
	public void canGetUserNamesAndPostCount() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		TypedQuery<Object[]> query = entityManager.createQuery(
	            "select u.userName, count(p.id) from User u " + 
	            "left join u.posts p group by u.userName", Object[].class);
	    List<Object[]> resultList = query.getResultList();
	    
	    assertEquals("loki2302", (String)resultList.get(0)[0]);
	    assertEquals(7L, ((Long)resultList.get(0)[1]).longValue());
	    
	    assertEquals("qwerty", (String)resultList.get(1)[0]);
	    assertEquals(3L, ((Long)resultList.get(1)[1]).longValue());
	    
	    assertEquals("lena", (String)resultList.get(2)[0]);
	    assertEquals(12L, ((Long)resultList.get(2)[1]).longValue());
	}
	
	@Test
	public void canGetUserNamesAndPostCountTyped() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		TypedQuery<UserNameAndPostCount> query = entityManager.createQuery(
				"select new com.loki2302.JPQLTest$UserNameAndPostCount(u.userName, count(p.id)) " +
	            "from User u " + 
	            "left join u.posts p group by u.userName", UserNameAndPostCount.class);
	    List<UserNameAndPostCount> resultList = query.getResultList();
	    
	    assertEquals("loki2302", resultList.get(0).userName);
	    assertEquals(7L, resultList.get(0).postCount);
	    
	    assertEquals("qwerty", resultList.get(1).userName);
	    assertEquals(3L, resultList.get(1).postCount);
	    
	    assertEquals("lena", resultList.get(2).userName);
	    assertEquals(12L, resultList.get(2).postCount);
	}
	
	public static class UserNameAndPostCount {
		public String userName;
		public long postCount;
		
		public UserNameAndPostCount(String userName, long postCount) {
			this.userName = userName;
			this.postCount = postCount;
		}
	}
}
