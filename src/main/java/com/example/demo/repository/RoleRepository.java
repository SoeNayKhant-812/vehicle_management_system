package com.example.demo.repository;

import com.example.demo.model.Role;
import com.example.demo.model.log_model.RoleLog;
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
public class RoleRepository {

	private static final Logger logger = LoggerFactory.getLogger(RoleRepository.class);

	private final DynamoDbEnhancedClient enhancedClient;
	private final DynamoDbTable<Role> roleTable;
	private final DynamoDbTable<RoleLog> roleLogTable;

	public RoleRepository(DynamoDbClient client) {
		this.enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(client).build();
		this.roleTable = enhancedClient.table("role", TableSchema.fromBean(Role.class));
		this.roleLogTable = enhancedClient.table("RoleLog", TableSchema.fromBean(RoleLog.class));
	}

	@PostConstruct
	public void createTablesIfNotExistsAndDefaults() {
		try {
			roleTable.createTable();
		} catch (Exception e) {
			logger.debug("Role table may already exist or creation failed: {}", e.getMessage());
		}
		try {
			roleLogTable.createTable();
		} catch (Exception e) {
			logger.debug("RoleLog table may already exist or creation failed: {}", e.getMessage());
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

	// Basic CRUD ----------------------------------------------------------

	public List<Role> findAll() {
		List<Role> roles = new ArrayList<>();
		roleTable.scan().items().forEach(roles::add);
		return roles;
	}

	public Optional<Role> findByName(String name) {
		return Optional
				.ofNullable(roleTable.getItem(r -> r.key(Key.builder().partitionValue(name.toUpperCase()).build())));
	}

	public boolean existsByName(String name) {
		return findByName(name).isPresent();
	}

	public void deleteByName(String name) {
		roleTable.deleteItem(DeleteItemEnhancedRequest.builder()
				.key(Key.builder().partitionValue(name.toUpperCase()).build()).build());
	}

	public Role save(Role role) {
		roleTable.putItem(PutItemEnhancedRequest.builder(Role.class).item(role).build());
		return role;
	}

	// Transactional operations with logs ----------------------------------

	public Role saveWithLog(Role role, RoleLog roleLog) {
		Expression notExists = Expression.builder().expression("attribute_not_exists(name)").build();

		TransactPutItemEnhancedRequest<Role> putRoleReq = TransactPutItemEnhancedRequest.builder(Role.class).item(role)
				.conditionExpression(notExists).build();

		TransactPutItemEnhancedRequest<RoleLog> putLogReq = TransactPutItemEnhancedRequest.builder(RoleLog.class)
				.item(roleLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(roleTable, putRoleReq).addPutItem(roleLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (saveWithLog) succeeded for roleName={} logId={}", role.getName(),
					roleLog.getId());
			return role;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (saveWithLog) for roleName={}, reasons: {}", role.getName(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to save role with log", ex);
		}
	}

	public Role updateWithLog(Role role, RoleLog roleLog) {
		Expression exists = Expression.builder().expression("attribute_exists(name)").build();

		TransactPutItemEnhancedRequest<Role> putRoleReq = TransactPutItemEnhancedRequest.builder(Role.class).item(role)
				.conditionExpression(exists).build();

		TransactPutItemEnhancedRequest<RoleLog> putLogReq = TransactPutItemEnhancedRequest.builder(RoleLog.class)
				.item(roleLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addPutItem(roleTable, putRoleReq).addPutItem(roleLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (updateWithLog) succeeded for roleName={} logId={}", role.getName(),
					roleLog.getId());
			return role;
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (updateWithLog) for roleName={}, reasons: {}", role.getName(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to update role with log", ex);
		}
	}

	public void deleteWithLog(Role role, RoleLog roleLog) {
		Expression exists = Expression.builder().expression("attribute_exists(name)").build();

		TransactDeleteItemEnhancedRequest deleteRoleReq = TransactDeleteItemEnhancedRequest.builder()
				.key(Key.builder().partitionValue(role.getName().toUpperCase()).build()).conditionExpression(exists)
				.build();

		TransactPutItemEnhancedRequest<RoleLog> putLogReq = TransactPutItemEnhancedRequest.builder(RoleLog.class)
				.item(roleLog).build();

		TransactWriteItemsEnhancedRequest transaction = TransactWriteItemsEnhancedRequest.builder()
				.addDeleteItem(roleTable, deleteRoleReq).addPutItem(roleLogTable, putLogReq).build();

		try {
			enhancedClient.transactWriteItems(transaction);
			logger.debug("Transact write (deleteWithLog) succeeded for roleName={} logId={}", role.getName(),
					roleLog.getId());
		} catch (TransactionCanceledException ex) {
			logger.error("Transact write canceled (deleteWithLog) for roleName={}, reasons: {}", role.getName(),
					ex.cancellationReasons(), ex);
			throw new com.example.demo.exception.TransactionFailureException("Failed to delete role with log", ex);
		}
	}
}
