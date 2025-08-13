package com.example.demo.service;

import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.exception.TransactionFailureException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.log_model.UserLog;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.log_service.UserLogService;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final IdGeneratorService idGenerator;
	private final BCryptPasswordEncoder passwordEncoder;
	private final RoleRepository roleRepository;
	private final UserLogService userLogService;

	public UserService(UserRepository userRepository, IdGeneratorService idGenerator,
			BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, UserLogService userLogService) {
		this.userRepository = userRepository;
		this.idGenerator = idGenerator;
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.userLogService = userLogService;
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

			try {
				createUserWithoutAuth(adminRequest);
				createUserWithoutAuth(userRequest);
				logger.info("Default admin and user accounts created initially!");
			} catch (Exception ex) {
				logger.error("Failed to create default users: {}", ex.getMessage(), ex);
			}
		}
	}

	private User getCurrentUser() {
		return getCurrentUserOrThrow();
	}

	public void logout(String username) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found for logout: " + username));
		user.setTokenValidAfter(Instant.now());

		userRepository.save(user);

		logger.info("User {} logged out. All tokens issued before {} are now invalid.", username,
				user.getTokenValidAfter());
	}

	public Optional<User> getCurrentUserOpt() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
			return Optional.empty();
		}
		String username = auth.getName();
		return userRepository.findAll().stream().filter(u -> u.getUsername().equals(username)).findFirst();
	}

	public User getCurrentUserOrThrow() {
		return getCurrentUserOpt().orElseThrow(() -> new UserNotFoundException("No authenticated user found"));
	}

	public List<User> getAllUsers() {
		logger.info("Fetching all users from the database.");
		return userRepository.findAll();
	}

	public User getUserById(String id) {
		logger.info("Fetching user with ID: {}", id);
		return userRepository.findById(id).orElseThrow(() -> {
			logger.warn("User not found with ID: {}", id);
			return new UserNotFoundException("User not found with ID: " + id);
		});
	}

	public User addUser(UserRegisterRequest request) throws TransactionFailureException {
	    validateUserRequest(request);
	    Optional<User> currentUserOpt = getCurrentUserOpt();

	    if (currentUserOpt.isPresent()) {
	        User currentUser = currentUserOpt.get();
	        String performedByUserId = currentUser.getId();
	        String performedByUsername = currentUser.getUsername();

	        logger.info("User '{}' is creating a new user with username '{}'", performedByUsername, request.getUsername());

	        String newUserId = idGenerator.generateUserId();
	        User newUser = new User();

	        newUser.setId(newUserId);
	        newUser.setUsername(request.getUsername());
	        newUser.setEmail(request.getEmail());
	        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
	        newUser.setCreatedAt(Instant.now());

	        Role role = roleRepository.findByName(request.getRole().toUpperCase())
	                .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRole()));
	        newUser.setRole(role);

	        UserLog log = userLogService.buildUserLog(newUser, "CREATE", performedByUserId, performedByUsername);

	        logger.info("Creating new user & log [userId={}, logId={}]", newUser.getId(), log.getId());

	        try {
	            return userRepository.saveWithLog(newUser, log);
	        } catch (RuntimeException ex) {
	            logger.error("Failed to create user with transactional log for userId={}: {}", newUser.getId(),
	                    ex.getMessage(), ex);
	            throw new TransactionFailureException("Failed to save user with log", ex);
	        }

	    } else {
	        logger.info("A new user with username '{}' is self-registering. Action will be logged by SYSTEM.", request.getUsername());
	        return createUserWithoutAuth(request);
	    }
	}

	private User createUserWithoutAuth(UserRegisterRequest request) throws TransactionFailureException {
		String userId = idGenerator.generateUserId();
		User user = new User();
		user.setId(userId);
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(Instant.now());

		Role role = roleRepository.findByName(request.getRole().toUpperCase())
				.orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRole()));
		user.setRole(role);

		UserLog log = userLogService.buildUserLog(user, "CREATE", "SYSTEM", "SYSTEM");
		return userRepository.saveWithLog(user, log);
	}

	public User updateUser(String id, UserRegisterRequest request) throws TransactionFailureException {
		validateUserRequest(request);

		User currentUser = getCurrentUser();
		String performedByUserId = currentUser.getId();
		String performedByUsername = currentUser.getUsername();

		User existingUser = userRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. User not found with ID: {}", id);
			return new UserNotFoundException("User with ID " + id + " not found");
		});

		existingUser.setUsername(request.getUsername());
		existingUser.setEmail(request.getEmail());
		existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

		Role role = roleRepository.findByName(request.getRole().toUpperCase())
				.orElseThrow(() -> new IllegalArgumentException("Invalid role: " + request.getRole()));
		existingUser.setRole(role);

		UserLog log = userLogService.buildUserLog(existingUser, "UPDATE", performedByUserId, performedByUsername);

		logger.info("Updating user & writing log [userId={}, logId={}]", existingUser.getId(), log.getId());

		try {
			return userRepository.updateWithLog(existingUser, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to update user with transactional log for userId={}: {}", existingUser.getId(),
					ex.getMessage(), ex);
			throw new TransactionFailureException("Failed to update user with log", ex);
		}
	}

	public void deleteUser(String id) throws TransactionFailureException {
		User existingUser = userRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot delete. User not found with ID: {}", id);
			return new UserNotFoundException("Cannot delete. User not found with ID: " + id);
		});

		User currentUser = getCurrentUser();
		String performedByUserId = currentUser.getId();
		String performedByUsername = currentUser.getUsername();

		UserLog log = userLogService.buildUserLog(existingUser, "DELETE", performedByUserId, performedByUsername);

		logger.info("Deleting user & writing log [userId={}, logId={}]", existingUser.getId(), log.getId());

		try {
			userRepository.deleteWithLog(existingUser, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to delete user with transactional log for userId={}: {}", existingUser.getId(),
					ex.getMessage(), ex);
			throw new TransactionFailureException("Failed to delete user with log", ex);
		}
	}

	private void validateUserRequest(UserRegisterRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("User data is required");
		}
		if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
			throw new IllegalArgumentException("Username is required");
		}
		if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
			throw new IllegalArgumentException("Email is required");
		}
		if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
			throw new IllegalArgumentException("Password is required");
		}
		if (request.getRole() == null || request.getRole().trim().isEmpty()) {
			throw new IllegalArgumentException("Role is required");
		}
	}
}
