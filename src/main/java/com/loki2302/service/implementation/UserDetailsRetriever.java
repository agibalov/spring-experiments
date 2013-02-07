package com.loki2302.service.implementation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Service;

import com.loki2302.entities.Post;
import com.loki2302.entities.User;

@Service
public class UserDetailsRetriever {
	@PersistenceContext EntityManager entityManager;
	
	public UserAndPostCount getUserDetails(long userId) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<UserAndPostCount> criteriaQuery = 
				criteriaBuilder.createQuery(UserAndPostCount.class);
		Root<User> root = criteriaQuery.from(User.class);
		Join<User, Post> postsExpression = root.join("posts", JoinType.LEFT);
		Expression<Long> postCountExpression = criteriaBuilder.count(postsExpression);
		criteriaQuery.multiselect(root, postCountExpression);
		criteriaQuery.groupBy(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("id"), userId));		
		TypedQuery<UserAndPostCount> typedQuery = entityManager.createQuery(criteriaQuery);
		UserAndPostCount result = typedQuery.getSingleResult();
		return result;
	}
}