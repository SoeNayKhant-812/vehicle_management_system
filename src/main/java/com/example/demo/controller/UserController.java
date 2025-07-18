package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.dto.UserRegisterRequest;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping
	public ResponseEntity<List<UserDTO>> getAllUsers() {
		List<User> users = userService.getAllUsers();
		List<UserDTO> dtos = users.stream().map(UserMapper::toDTO).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
		User user = userService.getUserById(id);
		return ResponseEntity.ok(UserMapper.toDTO(user));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PostMapping("/addUser")
	public ResponseEntity<UserDTO> addUser(@Valid @RequestBody UserRegisterRequest request) {
		User user = userService.addUser(request);
		return ResponseEntity.ok(UserMapper.toDTO(user));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PutMapping("/{id}/update")
	public ResponseEntity<UserDTO> updateUser(@PathVariable String id,
			@Valid @RequestBody UserRegisterRequest request) {
		User updatedUser = userService.updateUser(id, request);
		return ResponseEntity.ok(UserMapper.toDTO(updatedUser));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
		return ResponseEntity.ok("User deleted successfully");
	}
}
