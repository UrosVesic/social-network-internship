package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CommentRequest
{
	@NotEmpty(message = "Content field should not be empty.")
	private String content;
}
