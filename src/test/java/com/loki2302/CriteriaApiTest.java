package com.loki2302;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.loki2302.entities.Post;
import com.loki2302.entities.User;
import com.loki2302.repositories.PostRepository;
import com.loki2302.repositories.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml", "classpath:repository-context.xml"})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class CriteriaApiTest {
	
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;
	
	@Ignore
	@Test
	public void canGetUserNamesAndPostCount() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<User> root = criteriaQuery.from(User.class);
		Join<User, Post> postsExpression = root.join("posts");
		criteriaQuery.multiselect(root.get("userName"), criteriaBuilder.count(postsExpression));		
		
		TypedQuery<Tuple> typedQuery = entityManager.createQuery(criteriaQuery);
		List<Tuple> tupleList = typedQuery.getResultList();
		for(Tuple tuple : tupleList) {
			System.out.printf("%s\n", tuple.get(0));
		}
	}
	
	@Test
	public void canGetAllUserNames() {
		createUserWithPosts("loki2302", 7);
		createUserWithPosts("qwerty", 3);
		createUserWithPosts("lena", 12);
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
		Root<User> root = criteriaQuery.from(User.class);
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
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.orderBy(criteriaBuilder.asc(root.get("userName")));
		
		TypedQuery<User> typedQuery = entityManager.createQuery(criteriaQuery);
		List<User> userList = typedQuery.getResultList();
		assertEquals("lena", userList.get(0).getUserName());
		assertEquals("loki2302", userList.get(1).getUserName());
		assertEquals("qwerty", userList.get(2).getUserName());		
	}
	
	private void createUserWithPosts(String userName, int numberOfPosts) {
		User user = new User();
		user.setUserName(userName);
		user.setPassword("qwerty");
		user = userRepository.save(user);
		
		for(int i = 0; i < numberOfPosts; ++i) {
			Post post = new Post();
			post.setText(String.format("Post #%d of %s", i, userName));
			post.setText("content here");
			post.setAuthor(user);
			post = postRepository.save(post);
		}
	}

}
