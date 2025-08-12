package com.example.demo.service;

import com.example.demo.dto.RoleDTO;
import com.example.demo.exception.TransactionFailureException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.model.log_model.RoleLog;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.log_service.RoleLogService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

	private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

	private final RoleRepository roleRepository;
	private final RoleLogService roleLogService;
	private final UserService userService;

	public RoleService(RoleRepository roleRepository, RoleLogService roleLogService, UserService userService) {
		this.roleRepository = roleRepository;
		this.roleLogService = roleLogService;
		this.userService = userService;
	}

	private User getCurrentUser() {
		return userService.getCurrentUserOrThrow();
	}

	public List<RoleDTO> getAllRoles() {
		logger.info("Fetching all roles from the database.");
		return roleRepository.findAll().stream().map(role -> new RoleDTO(role.getName(), role.getDescription()))
				.collect(Collectors.toList());
	}

	public RoleDTO getRoleByName(String name) {
		logger.info("Fetching role with name: {}", name);
		Role role = roleRepository.findByName(name.toUpperCase())
				.orElseThrow(() -> new UserNotFoundException("Role not found: " + name));
		return new RoleDTO(role.getName(), role.getDescription());
	}

	public RoleDTO createRole(RoleDTO dto) throws TransactionFailureException {
		validateRoleDTO(dto);

		User currentUser = getCurrentUser();
		String performedByUserId = currentUser.getId();
		String performedByUsername = currentUser.getUsername();

		Role role = new Role();
		role.setName(dto.getName().toUpperCase());
		role.setDescription(dto.getDescription());

		RoleLog log = roleLogService.buildRoleLog(role, "CREATE", performedByUserId, performedByUsername);

		logger.info("Creating new role & log [roleName={}, logId={}]", role.getName(), log.getId());

		try {
			roleRepository.saveWithLog(role, log);
			return new RoleDTO(role.getName(), role.getDescription());
		} catch (RuntimeException ex) {
			logger.error("Failed to create role with transactional log for roleName={}: {}", role.getName(),
					ex.getMessage(), ex);
			throw new TransactionFailureException("Failed to save role with log", ex);
		}
	}

	public void deleteRole(String name) throws TransactionFailureException {
		Role role = roleRepository.findByName(name.toUpperCase())
				.orElseThrow(() -> new UserNotFoundException("Role not found: " + name));

		User currentUser = getCurrentUser();
		String performedByUserId = currentUser.getId();
		String performedByUsername = currentUser.getUsername();

		RoleLog log = roleLogService.buildRoleLog(role, "DELETE", performedByUserId, performedByUsername);

		logger.info("Deleting role & writing log [roleName={}, logId={}]", role.getName(), log.getId());

		try {
			roleRepository.deleteWithLog(role, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to delete role with transactional log for roleName={}: {}", role.getName(),
					ex.getMessage(), ex);
			throw new TransactionFailureException("Failed to delete role with log", ex);
		}
	}

	private void validateRoleDTO(RoleDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("Role data is required");
		}
		if (dto.getName() == null || dto.getName().trim().isEmpty()) {
			throw new IllegalArgumentException("Role name is required");
		}
		if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
			throw new IllegalArgumentException("Role description is required");
		}
	}
}
