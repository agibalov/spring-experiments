package com.loki2302.service;

import org.springframework.stereotype.Service;

import com.loki2302.dto.PostDTO;
import com.loki2302.entities.Post;
import com.loki2302.entities.User;

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