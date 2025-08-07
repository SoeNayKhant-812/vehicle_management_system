package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.RoleLog;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class RoleLogRepository {

    private final DynamoDbTable<RoleLog> roleLogTable;

    public RoleLogRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.roleLogTable = enhancedClient.table("RoleLog", TableSchema.fromBean(RoleLog.class));
    }

    public void save(RoleLog log) {
        roleLogTable.putItem(log);
    }

    @PostConstruct
    public void createTableIfNotExists() {
        try {
            roleLogTable.createTable();
        } catch (Exception e) {
            System.out.println("RoleLog table may already exist or creation failed: " + e.getMessage());
        }
    }

    public List<RoleLog> scanAll(int pageSize, Map<String, AttributeValue> startKey,
                                 List<RoleLog> results, Holder<Map<String, AttributeValue>> lastKeyHolder) {
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<RoleLog> pages = roleLogTable.scan(scanRequest);
        List<Page<RoleLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());

        if (!pageList.isEmpty()) {
            Page<RoleLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public List<RoleLog> queryWithFilters(Map<String, String> filters,
                                          Map<String, AttributeValue> startKey,
                                          int pageSize,
                                          Holder<Map<String, AttributeValue>> lastKeyHolder) {
        Expression expression = ExpressionBuilder.buildExpression(filters);

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(filters.get("id")).build()))
                .filterExpression(expression)
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<RoleLog> pages = roleLogTable.query(queryRequest);
        List<RoleLog> results = new ArrayList<>();

        List<Page<RoleLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());
        if (!pageList.isEmpty()) {
            Page<RoleLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public static class Holder<T> {
        public T value;
    }
}
