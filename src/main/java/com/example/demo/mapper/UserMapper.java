package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.Role;
import com.example.demo.model.User;

public class UserMapper {

	public static UserDTO toDTO(User user) {
		if (user == null) return null;

		return new UserDTO(
			user.getId(),
			user.getUsername(),
			user.getEmail(),
			user.getRole() != null ? user.getRole().getName() : null
		);
	}

	public static User toEntity(UserDTO dto) {
		if (dto == null) return null;

		User user = new User();
		user.setId(dto.getId());
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());

		if (dto.getRole() != null && !dto.getRole().isBlank()) {
			Role role = new Role();
			role.setName(dto.getRole().toUpperCase());
			user.setRole(role);
		}

		return user;
	}
}
