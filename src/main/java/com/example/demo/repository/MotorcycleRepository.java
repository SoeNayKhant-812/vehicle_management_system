package com.example.demo.repository;

import com.example.demo.model.Motorcycle;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

@Repository
public class MotorcycleRepository {

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Motorcycle> motorcycleTable;

	public MotorcycleRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.motorcycleTable = enhancedClient.table("motorcycle", TableSchema.fromBean(Motorcycle.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			motorcycleTable.createTable();
		} catch (Exception e) {
			System.out.println("DynamoDB table 'motorcycle' may already exist or creation failed: " + e.getMessage());
		}

	}

	public List<Motorcycle> findAll() {
		List<Motorcycle> motorcycles = new ArrayList<>();
		motorcycleTable.scan().items().forEach(motorcycles::add);
		return motorcycles;
	}

	public Optional<Motorcycle> findById(String id) {
		return Optional.ofNullable(motorcycleTable.getItem(r -> r.key(k -> k.partitionValue(id))));
	}

	public Motorcycle save(Motorcycle motorcycle) {
		motorcycleTable.putItem(motorcycle);
		return motorcycle;
	}

	public void deleteById(String id) {
		motorcycleTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}
}
