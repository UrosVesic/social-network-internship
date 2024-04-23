package com.levi9.internship.social.network.exceptions;

import lombok.Getter;

@Getter
public final class IAMProviderException extends Exception
{
	private final ErrorCode errorCode;

	public IAMProviderException(final String message)
	{
		super(message);
		this.errorCode = null;
	}

	public IAMProviderException(final ErrorCode errorCode, final String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	public IAMProviderException(final Throwable cause, final ErrorCode errorCode)
	{
		super(cause);
		this.errorCode = errorCode;
	}

	public IAMProviderException(final Throwable cause)
	{
		super(cause);
		this.errorCode = null;
	}
}
