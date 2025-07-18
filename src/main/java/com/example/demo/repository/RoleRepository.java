package com.example.demo.repository;

import com.example.demo.model.Role;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

@Repository
public class RoleRepository {

	private final DynamoDbTable<Role> roleTable;

	public RoleRepository(DynamoDbClient client) {
		DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();

		this.roleTable = enhancedClient.table("role", TableSchema.fromBean(Role.class));
	}

	@PostConstruct
	public void initTableAndDefaults() {
		try {
			roleTable.createTable();
		} catch (Exception e) {
			System.out.println("DynamoDB table 'role' may already exist or creation failed: " + e.getMessage());
		}

		if (!existsByName("ADMIN")) {
			save(new Role() {
				{
					setName("ADMIN");
					setDescription("Administrator role");
				}
			});
		}
		if (!existsByName("USER")) {
			save(new Role() {
				{
					setName("USER");
					setDescription("Default user role");
				}
			});
		}
	}

	public Role save(Role role) {
		roleTable.putItem(role);
		return role;
	}

	public List<Role> findAll() {
		List<Role> roles = new ArrayList<>();
		roleTable.scan().items().forEach(roles::add);
		return roles;
	}

	public Optional<Role> findByName(String name) {
		return Optional.ofNullable(roleTable.getItem(r -> r.key(k -> k.partitionValue(name.toUpperCase()))));
	}

	public boolean existsByName(String name) {
		return findByName(name).isPresent();
	}

	public void deleteByName(String name) {
		roleTable.deleteItem(r -> r.key(k -> k.partitionValue(name.toUpperCase())));
	}
}
