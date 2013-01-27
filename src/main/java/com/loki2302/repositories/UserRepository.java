package com.loki2302.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loki2302.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("select u from User u where u.userName = :name")
	User findUserByName(@Param("name") String userName);

}