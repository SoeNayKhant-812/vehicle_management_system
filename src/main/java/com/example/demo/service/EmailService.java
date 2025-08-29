package com.example.demo.service;

import com.example.demo.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	@Async
	@Retryable(retryFor = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 5000))
	public void sendWelcomeEmail(User user) {
		logger.info("Attempting to send welcome email to '{}'...", user.getEmail());
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setSubject("Welcome to the Vehicle Management System!");
			message.setText(
					"Hello " + user.getUsername() + ",\n\nWelcome aboard! Your account has been successfully created.");

			mailSender.send(message);
			logger.info("Successfully sent welcome email to '{}'", user.getEmail());
		} catch (MailException e) {
			// Log the exception, but rethrow it so the @Retryable aspect knows a failure
			// occurred.
			logger.warn("Failed to send welcome email on this attempt for user '{}'. Retrying if possible...",
					user.getUsername());
			throw e;
		}
	}

	@Async
	@Retryable(retryFor = { MailException.class }, maxAttempts = 3, backoff = @Backoff(delay = 5000))
	public void sendAccountUpdateEmail(User user) {
		logger.info("Attempting to send account update notification to '{}'...", user.getEmail());
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(user.getEmail());
			message.setSubject("Your VMS Account Has Been Updated");
			message.setText("Hello " + user.getUsername()
					+ ",\n\nThis is a notification that your account details have been updated.");

			mailSender.send(message);
			logger.info("Successfully sent account update notification to '{}'", user.getEmail());
		} catch (MailException e) {
			logger.warn("Failed to send account update email on this attempt for user '{}'. Retrying if possible...",
					user.getUsername());
			throw e;
		}
	}
}
