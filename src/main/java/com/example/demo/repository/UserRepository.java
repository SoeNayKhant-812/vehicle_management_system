package com.example.demo.repository;

import com.example.demo.model.User;
import com.example.demo.model.log_model.UserLog;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.TransactionCanceledException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

	private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<User> userTable;
	private final DynamoDbTable<UserLog> userLogTable;

	public UserRepository(DynamoDbClient dynamoDbClient) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
		this.userTable = enhancedClient.table("user", TableSchema.fromBean(User.class));
		this.userLogTable = enhancedClient.table("UserLog", TableSchema.fromBean(UserLog.class));
	}

	@PostConstruct
	public void createTablesIfNotExists() {
		try {
			userTable.createTable();
		} catch (Exception e) {
			logger.debug("User table may already exist or creation failed: {}", e.getMessage());
		}
		try {
			userLogTable.createTable();
		} catch (Exception e) {
			logger.debug("UserLog table may already exist or creation failed: {}", e.getMessage());
		}
	}

	// Basic CRUD ----------------------------------------------------------

	public List<User> findAll() {
		List<User> users = new ArrayList<>();
		userTable.scan().items().forEach(users::add);
		return users;
	}

	public Optional<User> findById(String id) {
		return Optional.ofNullable(userTable.getItem(r -> r.key(Key.builder().partitionValue(id).build())));
	}

	public User save(User user) {
		userTable.putItem(PutItemEnhancedRequest.builder(User.class).item(user).build());
		return user;
	}

	public void deleteById(String id) {
		userTable.deleteItem(DeleteItemEnhancedRequest.builder().key(Key.builder().partitionValue(id).build()).build());
	}

	public boolean existsById(String id) {
		return findById(id).isPresent();
	}

	// Transactional operations with logs ----------------------------------

	public User saveWithLog(User user, UserLog userLog) {
		Expression notExists = Expression.builder().expression("attribute_not_exists(id)").build();

		TransactPutItemEnhancedRequest<User> putUserReq = TransactPutItemEnhancedRequest.builder(User.class).item(user)
				.conditionExpression(notExists).build();

		TransactPutItemEnhancedRequest<UserLog> putLogReq = TransactPutItemEnhancedRequest.builder(UserLog.class)
				.item(userLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(userTable, putUserReq).addPutItem(userLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (saveWithLog) succeeded for userId={} logId={}", user.getId(),
					userLog.getId());
			return user;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (saveWithLog) for userId={}, reasons: {}", user.getId(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to save user with log", ex);
		}
	}

	public User updateWithLog(User user, UserLog userLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactPutItemEnhancedRequest<User> putUserReq = TransactPutItemEnhancedRequest.builder(User.class).item(user)
				.conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<UserLog> putLogReq = TransactPutItemEnhancedRequest.builder(UserLog.class)
				.item(userLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(userTable, putUserReq).addPutItem(userLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (updateWithLog) succeeded for userId={} logId={}", user.getId(),
					userLog.getId());
			return user;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (updateWithLog) for userId={}, reasons: {}", user.getId(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to update user with log", ex);
		}
	}

	public void deleteWithLog(User user, UserLog userLog) {
		Expression exists = Expression.builder().expression("attribute_exists(id)").build();

		TransactDeleteItemEnhancedRequest deleteUserReq = TransactDeleteItemEnhancedRequest.builder()
				.key(Key.builder().partitionValue(user.getId()).build()).conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<UserLog> putLogReq = TransactPutItemEnhancedRequest.builder(UserLog.class)
				.item(userLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addDeleteItem(userTable, deleteUserReq).addPutItem(userLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (deleteWithLog) succeeded for userId={} logId={}", user.getId(),
					userLog.getId());
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (deleteWithLog) for userId={}, reasons: {}", user.getId(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to delete user with log", ex);
		}
	}
}
