package com.levi9.internship.social.network.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSignInRequest
{

	@NotEmpty(message = "Username should not be empty.")
	private final String username;

	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$",
		message = """
			Password should contain: at least one numeric character, at least one lowercase character, \
			at least one uppercase character, at least one special symbol among @#$% and length should be between 8 and 20.\
			""")
	private final String password;
}
