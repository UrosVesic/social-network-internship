package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ConfirmUserSignUpRequest
{

	@NotEmpty(message = "Code should not be empty.")
	private String code;

	@NotEmpty(message = "Username should not be empty.")
	private String username;
}
