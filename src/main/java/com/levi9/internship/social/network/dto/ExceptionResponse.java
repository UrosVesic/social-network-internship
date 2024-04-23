package com.levi9.internship.social.network.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.levi9.internship.social.network.exceptions.ErrorCode;
import lombok.Data;

@Data
public class ExceptionResponse
{
	private String message;
	private ErrorCode errorCode;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime timestamp;

	public ExceptionResponse(final String message, final ErrorCode errorCode)
	{
		this.message = message;
		this.errorCode = errorCode;
		this.timestamp = LocalDateTime.now();
	}
}
