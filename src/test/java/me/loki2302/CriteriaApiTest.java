package me.loki2302;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import me.loki2302.entities.Post;
import me.loki2302.entities.XUser;

import org.junit.Test;

public class CriteriaApiTest extends AbstractSpringDataJPATest {	
	@Test
	public void canGetUserNamesAndPostCount() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<XUser> root = criteriaQuery.from(XUser.class);
		Join<XUser, Post> postsExpression = root.join("posts", JoinType.LEFT);
		Expression<Long> postCountExpression = criteriaBuilder.count(postsExpression);
		criteriaQuery.multiselect(root.get("userName"), postCountExpression);
		criteriaQuery.groupBy(root.get("userName"));
		criteriaQuery.orderBy(criteriaBuilder.asc(postCountExpression));
		
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Tuple> tupleList = typedQuery.getResultList();
		assertEquals("qwerty", tupleList.get(0).get(0));
		assertEquals(3L, tupleList.get(0).get(1));		
		assertEquals("loki2302", tupleList.get(1).get(0));
		assertEquals(7L, tupleList.get(1).get(1));		
		assertEquals("lena", tupleList.get(2).get(0));
		assertEquals(12L, tupleList.get(2).get(1));
	}
	
	@Test
	public void canGetUserNamesAndPostCountTyped() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserNameAndPostCount> criteriaQuery = 
				criteriaBuilder.createQuery(UserNameAndPostCount.class);
		Root<XUser> root = criteriaQuery.from(XUser.class);
		Join<XUser, Post> postsExpression = root.join("posts", JoinType.LEFT);
		Expression<Long> postCountExpression = criteriaBuilder.count(postsExpression);
		criteriaQuery.multiselect(root.get("userName"), postCountExpression);
		criteriaQuery.groupBy(root.get("userName"));
		criteriaQuery.orderBy(criteriaBuilder.asc(postCountExpression));
		
		TypedQuery<UserNameAndPostCount> typedQuery = entityManager.createQuery(criteriaQuery);
		List<UserNameAndPostCount> resultList = typedQuery.getResultList();
		assertEquals("qwerty", resultList.get(0).userName);
		assertEquals(3L, resultList.get(0).postCount);		
		assertEquals("loki2302", resultList.get(1).userName);
		assertEquals(7L, resultList.get(1).postCount);		
		assertEquals("lena", resultList.get(2).userName);
		assertEquals(12L, resultList.get(2).postCount);
	}
	
	@Test
	public void canGetAllUserNames() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<XUser> root = criteriaQuery.from(XUser.class);
		criteriaQuery.multiselect(root.get("userName"));
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("userName")));
		
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Tuple> tupleList = typedQuery.getResultList();
		assertEquals("lena", tupleList.get(0).get(0));
		assertEquals("loki2302", tupleList.get(1).get(0));
		assertEquals("qwerty", tupleList.get(2).get(0));
	}
	
	@Test
	public void canGetAllUsersSorted() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<XUser> criteriaQuery = criteriaBuilder.createQuery(XUser.class);
		Root<XUser> root = criteriaQuery.from(XUser.class);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("userName")));
		
		TypedQuery<XUser> typedQuery = entityManager.createQuery(criteriaQuery);
		List<XUser> userList = typedQuery.getResultList();
		assertEquals("lena", userList.get(0).getUserName());
		assertEquals("loki2302", userList.get(1).getUserName());
		assertEquals("qwerty", userList.get(2).getUserName());		
	}
}
