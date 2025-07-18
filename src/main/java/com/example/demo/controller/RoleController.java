package com.example.demo.controller;

import com.example.demo.dto.RoleDTO;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

	@Autowired
	private RoleService roleService;

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public ResponseEntity<List<RoleDTO>> getAllRoles() {
		return ResponseEntity.ok(roleService.getAllRoles());
	}

	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{name}")
	public ResponseEntity<RoleDTO> getRoleByName(@PathVariable String name) {
		return ResponseEntity.ok(roleService.getRoleByName(name));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
		return ResponseEntity.ok(roleService.createRole(roleDTO));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{name}")
	public ResponseEntity<String> deleteRole(@PathVariable String name) {
		roleService.deleteRole(name);
		return ResponseEntity.ok("Role deleted: " + name);
	}
}
