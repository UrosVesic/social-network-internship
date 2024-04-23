package com.levi9.internship.social.network.dto;


public record CustomResetPasswordRequest(String username, String confirmationCode, String password)
{
}
