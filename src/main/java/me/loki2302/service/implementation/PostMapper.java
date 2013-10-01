package me.loki2302.service.implementation;

import me.loki2302.dto.PostDTO;
import me.loki2302.entities.Post;
import me.loki2302.entities.User;

import org.springframework.stereotype.Service;


@Service
public class PostMapper {
	public PostDTO build(Post post) {
		PostDTO postDto = new PostDTO();
		postDto.PostId = post.getId();
		postDto.Text = post.getText();
		
		User author = post.getAuthor();
		postDto.UserId = author.getId();
		postDto.UserName = author.getUserName();
		
		return postDto;
	}
}