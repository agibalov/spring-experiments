package me.loki2302.impl;

import me.loki2302.impl.entities.Comment;
import me.loki2302.impl.entities.Post;
import me.loki2302.impl.entities.User;
import me.loki2302.impl.repositories.CommentRepository;
import me.loki2302.impl.repositories.PostRepository;
import me.loki2302.impl.repositories.UserRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlogService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public User createUser(String name) {
        User user = userRepository.findByName(name);
        if(user != null) {
            throw new UserAlreadyExistsException();
        }

        user = new User();
        user.name = name;
        return userRepository.save(user);
    }

    public User getUser(String name) {
        User user = userRepository.findByName(name);
        if(user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    public User getUser(long id) {
        User user = userRepository.findOne(id);
        if(user == null) {
            throw new UserNotFoundException();
        }

        return user;
    }

    public User updateUser(long userId, String name) {
        User user = userRepository.findOne(userId);
        if(user == null) {
            throw new UserNotFoundException();
        }

        user.name = name;
        return userRepository.save(user);
    }

    public void deleteUser(long userId) {
        User user = userRepository.findOne(userId);
        if(user == null) {
            throw new UserNotFoundException();
        }

        userRepository.delete(user);
    }



    public Post createPost(User user, String content) {
        Post post = new Post();
        post.content = content;
        post.user = user;
        return postRepository.save(post);
    }

    public void deletePost(long postId) {
        Post post = postRepository.findOne(postId);
        if(post == null) {
            throw new PostNotFoundException();
        }

        postRepository.delete(post);
    }


    public Comment createComment(long postId, String content) {
        Post post = postRepository.findOne(postId);
        if(post == null) {
            throw new PostNotFoundException();
        }

        Comment comment = new Comment();
        comment.post = post;
        comment.content = content;
        return commentRepository.save(comment);
    }

    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        if(comment == null) {
            throw new CommentNotFoundException();
        }

        commentRepository.delete(comment);
    }

}
