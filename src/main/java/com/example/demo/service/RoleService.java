package com.example.demo.service;

import com.example.demo.dto.RoleDTO;
import com.example.demo.model.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

	private final RoleRepository roleRepository;

	@Autowired
	public RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	public List<RoleDTO> getAllRoles() {
		return roleRepository.findAll().stream().map(role -> new RoleDTO(role.getName(), role.getDescription()))
				.collect(Collectors.toList());
	}

	public RoleDTO getRoleByName(String name) {
		Role role = roleRepository.findByName(name.toUpperCase())
				.orElseThrow(() -> new RuntimeException("Role not found: " + name));
		return new RoleDTO(role.getName(), role.getDescription());
	}

	public RoleDTO createRole(RoleDTO dto) {
		Role role = new Role();
		role.setName(dto.getName());
		role.setDescription(dto.getDescription());
		roleRepository.save(role);
		return new RoleDTO(role.getName(), role.getDescription());
	}

	public void deleteRole(String name) {
		roleRepository.deleteByName(name);
	}
}
