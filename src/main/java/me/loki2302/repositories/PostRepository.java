package me.loki2302.repositories;

import java.util.List;

import me.loki2302.entities.Post;
import me.loki2302.entities.XUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findAllByAuthor(XUser author);
	
	@Query("select count(p) from Post p where author = :author")
	long countByAuthor(@Param("author") XUser author);
}