package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class HidePostRequest
{

	@NotEmpty(message = "User id should not be empty!")
	private String userId;
}
