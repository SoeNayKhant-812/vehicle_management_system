package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private IdGeneratorService idGenerator;

	public List<User> getAllUsers() {
		logger.info("Fetching all users from the database.");
		return userRepository.findAll();
	}

	public User getUserById(String id) {
		logger.info("Fetching user with ID: {}", id);
		return userRepository.findById(id).orElseThrow(() -> {
			logger.warn("User not found with ID: {}", id);
			return new VehicleNotFoundException("User not found with ID: " + id);
		});
	}

	public User addUser(UserDTO dto) {
		String generatedId = idGenerator.generateUserId();
		User user = new User();
		user.setId(generatedId);
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setCreatedAt(Instant.now());

		logger.info("Creating new user [ID={}, Username={}, Email={}]", generatedId, dto.getUsername(), dto.getEmail());

		return userRepository.save(user);
	}

	public User updateUser(String id, UserDTO dto) {
		logger.info("Attempting to update user with ID: {}", id);
		User existingUser = userRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. User not found with ID: {}", id);
			return new VehicleNotFoundException("User with ID " + id + " not found");
		});

		existingUser.setUsername(dto.getUsername());
		existingUser.setEmail(dto.getEmail());

		logger.info("Successfully updated user with ID: {}", id);
		return userRepository.save(existingUser);
	}

	public void deleteUser(String id) {
		logger.info("Attempting to delete user with ID: {}", id);
		if (!userRepository.existsById(id)) {
			logger.warn("Cannot delete. User not found with ID: {}", id);
			throw new VehicleNotFoundException("Cannot delete. User not found with ID: " + id);
		}

		userRepository.deleteById(id);
		logger.info("Successfully deleted user with ID: {}", id);
	}
}
