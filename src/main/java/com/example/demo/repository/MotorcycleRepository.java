package com.example.demo.repository;

import com.example.demo.exception.TransactionFailureException;
import com.example.demo.model.Motorcycle;
import com.example.demo.model.log_model.MotorcycleLog;
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
public class MotorcycleRepository {

	private static final Logger logger = LoggerFactory.getLogger(MotorcycleRepository.class);

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Motorcycle> motorcycleTable;
	private final DynamoDbTable<MotorcycleLog> motorcycleLogTable;

	public MotorcycleRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.motorcycleTable = enhancedClient.table("motorcycle", TableSchema.fromBean(Motorcycle.class));
		this.motorcycleLogTable = enhancedClient.table("MotorcycleLog", TableSchema.fromBean(MotorcycleLog.class));
	}

	@PostConstruct
	public void createTableIfNotExists() {
		try {
			motorcycleTable.createTable();
		} catch (Exception e) {
			logger.debug("motorcycle table may already exist or creation failed: {}", e.getMessage());
		}

		try {
			motorcycleLogTable.createTable();
		} catch (Exception e) {
			logger.debug("MotorcycleLog table may already exist or creation failed: {}", e.getMessage());
		}
	}

	// ---------------------- Basic CRUD ----------------------
	public List<Motorcycle> findAll() {
		List<Motorcycle> motorcycles = new ArrayList<>();
		motorcycleTable.scan().items().forEach(motorcycles::add);
		return motorcycles;
	}

	public Optional<Motorcycle> findById(String id) {
		return Optional.ofNullable(motorcycleTable.getItem(r -> r.key(Key.builder().partitionValue(id).build())));
	}

	public Motorcycle save(Motorcycle motorcycle) {
		motorcycleTable.putItem(PutItemEnhancedRequest.builder(Motorcycle.class).item(motorcycle).build());
		return motorcycle;
	}

	public void deleteById(String id) {
		motorcycleTable
				.deleteItem(DeleteItemEnhancedRequest.builder().key(Key.builder().partitionValue(id).build()).build());
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}

	// ---------------------- Transactional ----------------------

	public Motorcycle saveWithLog(Motorcycle motorcycle, MotorcycleLog motorcycleLog) {
		Expression notExists = Expression.builder().expression("attribute_not_exists(id)").build();

		TransactPutItemEnhancedRequest<Motorcycle> putMotorcycleReq = TransactPutItemEnhancedRequest
				.builder(Motorcycle.class).item(motorcycle).conditionExpression(notExists).build();

		TransactPutItemEnhancedRequest<MotorcycleLog> putLogReq = TransactPutItemEnhancedRequest
				.builder(MotorcycleLog.class).item(motorcycleLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(motorcycleTable, putMotorcycleReq).addPutItem(motorcycleLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (saveWithLog) succeeded for motorcycleId={} logId={}", motorcycle.getId(),
					motorcycleLog.getId());
			return motorcycle;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (saveWithLog) for motorcycleId={}, reasons: {}", motorcycle.getId(),
					ex.cancellationReasons(), ex);
			throw new TransactionFailureException("Failed to save motorcycle with log", ex);
		}
	}

	public Motorcycle updateWithLog(Motorcycle motorcycle, MotorcycleLog motorcycleLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactPutItemEnhancedRequest<Motorcycle> putMotorcycleReq = TransactPutItemEnhancedRequest
				.builder(Motorcycle.class).item(motorcycle).conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<MotorcycleLog> putLogReq = TransactPutItemEnhancedRequest
				.builder(MotorcycleLog.class).item(motorcycleLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(motorcycleTable, putMotorcycleReq).addPutItem(motorcycleLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (updateWithLog) succeeded for motorcycleId={} logId={}", motorcycle.getId(),
					motorcycleLog.getId());
			return motorcycle;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (updateWithLog) for motorcycleId={}, reasons: {}", motorcycle.getId(),
					ex.cancellationReasons(), ex);
			throw new TransactionFailureException("Failed to update motorcycle with log", ex);
		}
	}

	public void deleteWithLog(Motorcycle motorcycle, MotorcycleLog motorcycleLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactDeleteItemEnhancedRequest deleteMotorcycleReq = TransactDeleteItemEnhancedRequest.builder()
				.key(Key.builder().partitionValue(motorcycle.getId()).build()).conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<MotorcycleLog> putLogReq = TransactPutItemEnhancedRequest
				.builder(MotorcycleLog.class).item(motorcycleLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addDeleteItem(motorcycleTable, deleteMotorcycleReq).addPutItem(motorcycleLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (deleteWithLog) succeeded for motorcycleId={} logId={}", motorcycle.getId(),
					motorcycleLog.getId());
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (deleteWithLog) for motorcycleId={}, reasons: {}", motorcycle.getId(),
					ex.cancellationReasons(), ex);
			throw new TransactionFailureException("Failed to delete motorcycle with log", ex);
		}
	}
}
