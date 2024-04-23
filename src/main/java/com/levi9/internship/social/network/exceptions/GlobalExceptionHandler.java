package com.levi9.internship.social.network.exceptions;

import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.levi9.internship.social.network.dto.ExceptionResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
//@ControllerAdvice
public class GlobalExceptionHandler
{
	@ExceptionHandler(value = IAMProviderException.class)
	public ResponseEntity<ExceptionResponse> IAMProviderException(final IAMProviderException ex)
	{
		log.error("Error: {} : {}", ex.getErrorCode(), ex.getMessage());
		final ExceptionResponse response = new ExceptionResponse(ex.getMessage(), ex.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(value = AuthenticationException.class)
	public ResponseEntity<ExceptionResponse> authenticationException(final AuthenticationException ex)
	{
		log.error("Unauthorized request error: {}", ex.getMessage());
		final ExceptionResponse response = new ExceptionResponse(ex.getMessage(), ErrorCode.ERROR_ACCESS_DENIED);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(value = BusinessException.class)
	public ResponseEntity<ExceptionResponse> businessException(final BusinessException ex)
	{
		log.error("Error: {} : {}", ex.getErrorCode(), ex.getMessage());
		final ExceptionResponse response = new ExceptionResponse(ex.getMessage(), ex.getErrorCode());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> methodArgumentNotValidException(final MethodArgumentNotValidException ex)
	{
		final String fieldsErrorMessages =
			ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(" "));
		log.error("Invalid arguments error: {}", fieldsErrorMessages);
		final ExceptionResponse response = new ExceptionResponse(fieldsErrorMessages, ErrorCode.ERROR_INVALID_PARAMETERS);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleUnauthorizedRequest(final AccessDeniedException ex)
	{
		log.error("Access denied error: {}", ex.getMessage());
		final ExceptionResponse response = new ExceptionResponse(ex.getMessage(), ErrorCode.ERROR_ACCESS_DENIED);
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ExceptionResponse> handleRuntimeException(final RuntimeException ex)
	{
		log.error("Runtime exception: {}", ex.getMessage());
		final ExceptionResponse response = new ExceptionResponse(ex.getMessage(), ErrorCode.RUNTIME_EXCEPTION_OCCURED);
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleException(final Exception ex)
	{
		log.error("Unhandled exception: {}", ex.getMessage());
		final ExceptionResponse response = new ExceptionResponse(ex.getMessage(), ErrorCode.UNHANDLED_EXCEPTION_OCCURED);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
