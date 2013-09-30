package me.loki2302.repositories;

import me.loki2302.entities.XUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<XUser, Long> {
	
	@Query("select u from XUser u where u.userName = :name")
	XUser findUserByName(@Param("name") String userName);

}