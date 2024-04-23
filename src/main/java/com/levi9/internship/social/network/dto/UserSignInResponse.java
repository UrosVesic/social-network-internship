package com.levi9.internship.social.network.dto;

import lombok.Data;

@Data
public class UserSignInResponse
{

	private String idToken;
	private String refreshToken;
	private String tokenType;
	private Integer expiresIn;
	private String accessToken;
}
