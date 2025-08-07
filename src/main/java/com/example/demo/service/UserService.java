package com.example.demo.service;

import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final IdGeneratorService idGenerator;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;

	@Autowired
	public UserService(UserRepository userRepository, IdGeneratorService idGenerator, PasswordEncoder passwordEncoder,
			RoleRepository roleRepository) {
		this.userRepository = userRepository;
		this.idGenerator = idGenerator;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
	}

	@PostConstruct
	public void initDefaultUsers() {
		if (userRepository.findAll().isEmpty()) {
			UserRegisterRequest adminRequest = new UserRegisterRequest();
			adminRequest.setUsername("admin");
			adminRequest.setPassword("adminpass");
			adminRequest.setEmail("admin@example.com");
			adminRequest.setRole("ADMIN");

			UserRegisterRequest userRequest = new UserRegisterRequest();
			userRequest.setUsername("user");
			userRequest.setPassword("userpass");
			userRequest.setEmail("user@example.com");
			userRequest.setRole("USER");

			addUser(adminRequest);
			addUser(userRequest);

			System.out.println("Default admin and user accounts created initially!");
		}
	}

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

	public User addUser(UserRegisterRequest request) {
		String userId = idGenerator.generateUserId();
		User user = new User();

		user.setId(userId);
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(Instant.now());
		// Set role from DB (case-insensitive)
		String roleName = request.getRole().toUpperCase();
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new IllegalArgumentException("Invalid role: " + roleName));
		user.setRole(role);
		logger.info("Registering new user [ID={}, Username={}, Email={}]", userId, user.getUsername(), user.getEmail());

		return userRepository.save(user);
	}

	public User updateUser(String id, UserRegisterRequest request) {
		logger.info("Attempting to update user with ID: {}", id);
		User existingUser = userRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. User not found with ID: {}", id);
			return new VehicleNotFoundException("User with ID " + id + " not found");
		});

		existingUser.setUsername(request.getUsername());
		existingUser.setEmail(request.getEmail());
		existingUser.setPassword(passwordEncoder.encode(request.getPassword()));
		// Update role if present
		String roleName = request.getRole().toUpperCase();
		Role role = roleRepository.findByName(roleName)
				.orElseThrow(() -> new IllegalArgumentException("Invalid role: " + roleName));
		existingUser.setRole(role);
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
