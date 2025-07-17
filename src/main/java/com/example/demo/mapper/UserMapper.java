package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;

public class UserMapper {

	public static UserDTO toDTO(User user) {
		if (user == null) {
			return null;
		}

		return new UserDTO(user.getId(), user.getUsername(), user.getEmail());
	}

	public static User toEntity(UserDTO dto) {
		if (dto == null) {
			return null;
		}

		User user = new User();
		user.setId(dto.getId());
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		return user;
	}
}
