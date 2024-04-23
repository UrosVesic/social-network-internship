package com.levi9.internship.social.network.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException
{
	private final ErrorCode errorCode;

	public BusinessException(final String message)
	{
		super(message);
		this.errorCode = null;
	}

	public BusinessException(final ErrorCode errorCode, final String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	public BusinessException(final Throwable cause)
	{
		super(cause);
		this.errorCode = null;
	}
}
