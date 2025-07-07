package com.example.demo.exception;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

	@ExceptionHandler(VehicleNotFoundException.class)
	public ResponseEntity<ExceptionResponseBody> handleVehicleNotFound(VehicleNotFoundException exception) {
		ExceptionResponseBody response = new ExceptionResponseBody(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
				exception.getMessage());
		logger.log(Level.WARNING, response.toString());
		return new ResponseEntity<ExceptionResponseBody>(response, HttpStatus.NOT_FOUND);
	}
}
