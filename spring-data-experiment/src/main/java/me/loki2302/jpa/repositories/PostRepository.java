package me.loki2302.jpa.repositories;

import java.util.List;

import me.loki2302.jpa.entities.Post;
import me.loki2302.jpa.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, QueryDslPredicateExecutor<Post> {
	List<Post> findAllByAuthor(User author);
	
	@Query("select count(p) from Post p where author = :author")
	long countByAuthor(@Param("author") User author);
}