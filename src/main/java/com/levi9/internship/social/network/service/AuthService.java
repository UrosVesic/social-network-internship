package com.levi9.internship.social.network.service;

import com.levi9.internship.social.network.dto.*;
import com.levi9.internship.social.network.exceptions.IAMProviderException;
import com.levi9.internship.social.network.model.User;

public interface AuthService
{
	void signUp(final UserSignUpRequest request) throws IAMProviderException;

	UserSignInResponse signIn(final UserSignInRequest request) throws IAMProviderException;

	void forgotPassword(final CustomForgotPasswordRequest request) throws IAMProviderException;

	void signOut(final String token) throws IAMProviderException;

	User getCurrentUser();

	void resetPassword(CustomResetPasswordRequest request) throws IAMProviderException;
}