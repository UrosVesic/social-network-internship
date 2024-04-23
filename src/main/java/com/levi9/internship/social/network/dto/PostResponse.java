package com.levi9.internship.social.network.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class PostResponse
{

	private Long id;
	private String content;
	String presignedUrl;
	private String createdBy;
	private Long groupId;
	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;
	private boolean isPrivate;
	private List<CommentResponse> comments;
}
