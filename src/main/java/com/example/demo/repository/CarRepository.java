package com.example.demo.repository;

import com.example.demo.model.Car;
import com.example.demo.model.log_model.CarLog;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.*;

@Repository
public class CarRepository {

	private static final Logger logger = LoggerFactory.getLogger(CarRepository.class);

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Car> carTable;
	private final DynamoDbTable<CarLog> carLogTable;

	public CarRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.carTable = enhancedClient.table("car", TableSchema.fromBean(Car.class));
		this.carLogTable = enhancedClient.table("CarLog", TableSchema.fromBean(CarLog.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			carTable.createTable();
		} catch (Exception e) {
			logger.debug("car table may already exist or creation failed: {}", e.getMessage());
		}

		try {
			carLogTable.createTable();
		} catch (Exception e) {
			logger.debug("CarLog table may already exist or creation failed: {}", e.getMessage());
		}
	}

	// Basic CRUD ----------------------------------------------------------

    public long count() {
        return carTable.scan().items().stream().count();
    }
    
	public List<Car> findAll() {
		List<Car> cars = new ArrayList<>();
		carTable.scan().items().forEach(cars::add);
		return cars;
	}

	public Optional<Car> findById(String id) {
		return Optional.ofNullable(carTable.getItem(r -> r.key(Key.builder().partitionValue(id).build())));
	}

	public Car save(Car car) {
		carTable.putItem(PutItemEnhancedRequest.builder(Car.class).item(car).build());
		return car;
	}

	public void deleteById(String id) {
		carTable.deleteItem(DeleteItemEnhancedRequest.builder().key(Key.builder().partitionValue(id).build()).build());
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}

	// Transactional operations using TransactWriteItemsEnhancedRequest

	public Car saveWithLog(Car car, CarLog carLog) {
		// condition: car must not exist
		Expression notExists = Expression.builder().expression("attribute_not_exists(id)").build();

		TransactPutItemEnhancedRequest<Car> putCarReq = TransactPutItemEnhancedRequest.builder(Car.class).item(car)
				.conditionExpression(notExists).build();

		TransactPutItemEnhancedRequest<CarLog> putLogReq = TransactPutItemEnhancedRequest.builder(CarLog.class)
				.item(carLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(carTable, putCarReq).addPutItem(carLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (saveWithLog) succeeded for carId={} logId={}", car.getId(), carLog.getId());
			return car;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (saveWithLog) for carId={}, reasons: {}", car.getId(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to save car with log", ex);
		}
	}

	public Car updateWithLog(Car car, CarLog carLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactPutItemEnhancedRequest<Car> putCarReq = TransactPutItemEnhancedRequest.builder(Car.class).item(car)
				.conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<CarLog> putLogReq = TransactPutItemEnhancedRequest.builder(CarLog.class)
				.item(carLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(carTable, putCarReq).addPutItem(carLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (updateWithLog) succeeded for carId={} logId={}", car.getId(), carLog.getId());
			return car;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (updateWithLog) for carId={}, reasons: {}", car.getId(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to update car with log", ex);
		}
	}

	public void deleteWithLog(Car car, CarLog carLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactDeleteItemEnhancedRequest deleteCarReq = TransactDeleteItemEnhancedRequest.builder()
				.key(Key.builder().partitionValue(car.getId()).build()).conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<CarLog> putLogReq = TransactPutItemEnhancedRequest.builder(CarLog.class)
				.item(carLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addDeleteItem(carTable, deleteCarReq).addPutItem(carLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (deleteWithLog) succeeded for carId={} logId={}", car.getId(), carLog.getId());
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (deleteWithLog) for carId={}, reasons: {}", car.getId(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to delete car with log", ex);
		}
	}
}
