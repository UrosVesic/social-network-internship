package com.levi9.internship.social.network.dto;

import java.util.Map;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserSignUpRequest
{

	@NotEmpty(message = "Username should not be empty.")
	@Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,18}[a-zA-Z0-9]$",
		message = """
			Username requirements: 1. Username consists of alphanumeric characters (a-zA-Z0-9), lowercase, or uppercase.\
			2. Username allowed of the dot (.), underscore (_), and hyphen (-). \
			3. The dot (.), underscore (_), or hyphen (-) must not be the first or last character. \
			4. The dot (.), underscore (_), or hyphen (-) does not appear consecutively, e.g., java..regex. \
			5. The number of characters must be between 5 to 20.\
			""")
	private String username;

	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20}$",
		message = """
			Password should contain: at least one numeric character, at least one lowercase character, \
			at least one uppercase character, at least one special symbol among @#$% and length should be between 8 and 20.\
			""")
	private String password;

	@NotNull(message = "Attributes should not be empty.")
	private Map<String, String> attributes;
}
