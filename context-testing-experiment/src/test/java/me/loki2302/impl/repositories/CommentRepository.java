package me.loki2302.impl.repositories;

import me.loki2302.impl.entities.Comment;
import me.loki2302.impl.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
