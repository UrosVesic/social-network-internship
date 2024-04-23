package com.levi9.internship.social.network.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.levi9.internship.social.network.dto.UserSignInRequest;
import com.levi9.internship.social.network.dto.UserSignInResponse;
import com.levi9.internship.social.network.dto.UserSignUpRequest;
import com.levi9.internship.social.network.dto.CustomForgotPasswordRequest;
import com.levi9.internship.social.network.dto.CustomResetPasswordRequest;
import com.levi9.internship.social.network.exceptions.IAMProviderException;
import com.levi9.internship.social.network.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController
{
	private final AuthService authService;

	public AuthController(final AuthService authService)
	{
		this.authService = authService;
	}

	@PostMapping(value = "/sign-up", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> signUp(@RequestBody @Valid final UserSignUpRequest request) throws IAMProviderException
	{
		authService.signUp(request);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping(value = "/sign-in", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<UserSignInResponse> signIn(@RequestBody @Valid final UserSignInRequest request)
		throws IAMProviderException
	{
		return new ResponseEntity<>(authService.signIn(request), HttpStatus.OK);
	}

	@PostMapping(value = "/forgot-password", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> forgotPassword(@RequestBody @Valid final CustomForgotPasswordRequest request)
		throws IAMProviderException
	{
		authService.forgotPassword(request);
		return new ResponseEntity<>("Password reset code successfully sent, check your email address", HttpStatus.OK);
	}

	@PostMapping(value = "/reset-password", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<String> resetPassword(@RequestBody @Valid final CustomResetPasswordRequest request)
		throws IAMProviderException
	{
		authService.resetPassword(request);
		return new ResponseEntity<>("Password reset successful", HttpStatus.OK);
	}

	@SecurityRequirement(name = "Bearer Authentication")
	@GetMapping(value = "/sign-out")
	public ResponseEntity<Void> signOut(@RequestHeader(HttpHeaders.AUTHORIZATION) final String authorizationHeader)
		throws IAMProviderException
	{
		final String jwt = authorizationHeader.replace("Bearer", "").trim();
		authService.signOut(jwt);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
