package com.example.demo.repository;

import com.example.demo.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

@Repository
public class UserRepository {

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<User> userTable;

	public UserRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.userTable = enhancedClient.table("user", TableSchema.fromBean(User.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			userTable.createTable();
		} catch (Exception e) {
			System.out.println("DynamoDB table 'user' may already exist or creation failed: " + e.getMessage());
		}
	}

	public List<User> findAll() {
		List<User> users = new ArrayList<>();
		userTable.scan().items().forEach(users::add);
		return users;
	}

	public Optional<User> findById(String id) {
		return Optional.ofNullable(userTable.getItem(r -> r.key(k -> k.partitionValue(id))));
	}

	public User save(User user) {
		userTable.putItem(user);
		return user;
	}

	public void deleteById(String id) {
		userTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}
}
