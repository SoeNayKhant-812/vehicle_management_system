package com.example.demo.repository;

import com.example.demo.model.Truck;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

@Repository
public class TruckRepository {

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Truck> truckTable;

	public TruckRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.truckTable = enhancedClient.table("truck", TableSchema.fromBean(Truck.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			truckTable.createTable();
		} catch (Exception e) {
			System.out.println("DynamoDB table 'truck' may already exist or creation failed: " + e.getMessage());
		}

	}

	public List<Truck> findAll() {
		List<Truck> trucks = new ArrayList<>();
		truckTable.scan().items().forEach(trucks::add);
		return trucks;
	}

	public Optional<Truck> findById(String id) {
		return Optional.ofNullable(truckTable.getItem(r -> r.key(k -> k.partitionValue(id))));
	}

	public Truck save(Truck truck) {
		truckTable.putItem(truck);
		return truck;
	}

	public void deleteById(String id) {
		truckTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}
}
