package me.loki2302;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import me.loki2302.entities.Post;
import me.loki2302.entities.User;
import me.loki2302.repositories.PostRepository;
import me.loki2302.repositories.UserRepository;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
public abstract class AbstractSpringDataJPATest {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	PostRepository postRepository;

	public static class UserNameAndPostCount {
		public String userName;
		public long postCount;
		
		public UserNameAndPostCount(String userName, long postCount) {
			this.userName = userName;
			this.postCount = postCount;
		}
	}
	
	protected void createUserWithPosts(String userName, int numberOfPosts) {
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
