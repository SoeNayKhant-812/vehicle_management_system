package com.example.demo.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(VehicleNotFoundException.class)
	public ResponseEntity<ExceptionResponseBody> handleVehicleNotFound(VehicleNotFoundException exception) {
		return buildResponse(exception, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(Exception.class)  // Optional: handles any unhandled exceptions
	public ResponseEntity<ExceptionResponseBody> handleGenericException(Exception exception) {
		return buildResponse(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<ExceptionResponseBody> buildResponse(Exception ex, HttpStatus status) {
		ExceptionResponseBody response = new ExceptionResponseBody(
				LocalDateTime.now(),
				status.value(),
				ex.getMessage()
		);

		logger.error("Exception caught [{}]: {}", ex.getClass().getSimpleName(), response.getMessage(), ex);

		return new ResponseEntity<>(response, status);
	}
}
