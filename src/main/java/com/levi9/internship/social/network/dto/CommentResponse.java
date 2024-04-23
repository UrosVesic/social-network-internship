package com.levi9.internship.social.network.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentResponse
{
	private Long id;
	private String content;
	private LocalDateTime createdAt;
	private Long parentId;
	private String userId;
	private Long postId;
}
