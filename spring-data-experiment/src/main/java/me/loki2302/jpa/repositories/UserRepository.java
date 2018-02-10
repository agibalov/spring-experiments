package me.loki2302.jpa.repositories;

import me.loki2302.jpa.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long>, QueryDslPredicateExecutor<User> {	
	@Query("select u from User u where u.userName = :name")
	User findUserByName(@Param("name") String userName);
}