package com.example.demo.repository;

import com.example.demo.model.Car;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.*;

@Repository
public class CarRepository {

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Car> carTable;

	public CarRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.carTable = enhancedClient.table("car", TableSchema.fromBean(Car.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			carTable.createTable();
		} catch (Exception e) {
			System.out.println("DynamoDB table 'car' may already exist or creation failed: " + e.getMessage());
		}

	}

	public List<Car> findAll() {
		List<Car> cars = new ArrayList<>();
		carTable.scan().items().forEach(cars::add);
		return cars;
	}

	public Optional<Car> findById(String id) {
		return Optional.ofNullable(carTable.getItem(r -> r.key(k -> k.partitionValue(id))));
	}

	public Car save(Car car) {
		carTable.putItem(car);
		return car;
	}

	public void deleteById(String id) {
		carTable.deleteItem(r -> r.key(k -> k.partitionValue(id)));
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}
}
