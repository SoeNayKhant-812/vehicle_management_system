package com.example.demo.repository;

import com.example.demo.exception.TransactionFailureException;
import com.example.demo.model.Truck;
import com.example.demo.model.log_model.TruckLog;
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
public class TruckRepository {

	private static final Logger logger = LoggerFactory.getLogger(TruckRepository.class);

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Truck> truckTable;
	private final DynamoDbTable<TruckLog> truckLogTable;

	public TruckRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.truckTable = enhancedClient.table("truck", TableSchema.fromBean(Truck.class));
		this.truckLogTable = enhancedClient.table("TruckLog", TableSchema.fromBean(TruckLog.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			truckTable.createTable();
		} catch (Exception e) {
			logger.debug("truck table may already exist or creation failed: {}", e.getMessage());
		}

		try {
			truckLogTable.createTable();
		} catch (Exception e) {
			logger.debug("TruckLog table may already exist or creation failed: {}", e.getMessage());
		}
	}

	// ---------------------- Basic CRUD ----------------------
	public List<Truck> findAll() {
		List<Truck> trucks = new ArrayList<>();
		truckTable.scan().items().forEach(trucks::add);
		return trucks;
	}

	public Optional<Truck> findById(String id) {
		return Optional.ofNullable(truckTable.getItem(r -> r.key(Key.builder().partitionValue(id).build())));
	}

	public Truck save(Truck truck) {
		truckTable.putItem(PutItemEnhancedRequest.builder(Truck.class).item(truck).build());
		return truck;
	}

	public void deleteById(String id) {
		truckTable
				.deleteItem(DeleteItemEnhancedRequest.builder().key(Key.builder().partitionValue(id).build()).build());
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}

	// ---------------------- Transactional ----------------------
	public Truck saveWithLog(Truck truck, TruckLog truckLog) {
		Expression notExists = Expression.builder().expression("attribute_not_exists(id)").build();

		TransactPutItemEnhancedRequest<Truck> putTruckReq = TransactPutItemEnhancedRequest.builder(Truck.class)
				.item(truck).conditionExpression(notExists).build();

		TransactPutItemEnhancedRequest<TruckLog> putLogReq = TransactPutItemEnhancedRequest.builder(TruckLog.class)
				.item(truckLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(truckTable, putTruckReq).addPutItem(truckLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (saveWithLog) succeeded for truckId={} logId={}", truck.getId(),
					truckLog.getId());
			return truck;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (saveWithLog) for truckId={}, reasons: {}", truck.getId(),
					ex.cancellationReasons(), ex);
			throw new TransactionFailureException("Failed to save truck with log", ex);
		}
	}

	public Truck updateWithLog(Truck truck, TruckLog truckLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactPutItemEnhancedRequest<Truck> putTruckReq = TransactPutItemEnhancedRequest.builder(Truck.class)
				.item(truck).conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<TruckLog> putLogReq = TransactPutItemEnhancedRequest.builder(TruckLog.class)
				.item(truckLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(truckTable, putTruckReq).addPutItem(truckLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (updateWithLog) succeeded for truckId={} logId={}", truck.getId(),
					truckLog.getId());
			return truck;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (updateWithLog) for truckId={}, reasons: {}", truck.getId(),
					ex.cancellationReasons(), ex);
			throw new TransactionFailureException("Failed to update truck with log", ex);
		}
	}

	public void deleteWithLog(Truck truck, TruckLog truckLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactDeleteItemEnhancedRequest deleteTruckReq = TransactDeleteItemEnhancedRequest.builder()
				.key(Key.builder().partitionValue(truck.getId()).build()).conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<TruckLog> putLogReq = TransactPutItemEnhancedRequest.builder(TruckLog.class)
				.item(truckLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addDeleteItem(truckTable, deleteTruckReq).addPutItem(truckLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (deleteWithLog) succeeded for truckId={} logId={}", truck.getId(),
					truckLog.getId());
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (deleteWithLog) for truckId={}, reasons: {}", truck.getId(),
					ex.cancellationReasons(), ex);
			throw new TransactionFailureException("Failed to delete truck with log", ex);
		}
	}
}
