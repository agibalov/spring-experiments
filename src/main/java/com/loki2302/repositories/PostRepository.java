package com.loki2302.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.loki2302.entities.Post;
import com.loki2302.entities.User;

public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findAllByAuthor(User author);
	
	@Query("select count(p) from Post p where author = :author")
	long countByAuthor(@Param("author") User author);
}