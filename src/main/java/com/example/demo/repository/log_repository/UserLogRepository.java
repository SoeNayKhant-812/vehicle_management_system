package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.UserLog;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class UserLogRepository {

    private final DynamoDbTable<UserLog> userLogTable;

    public UserLogRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.userLogTable = enhancedClient.table("UserLog", TableSchema.fromBean(UserLog.class));
    }

    public void save(UserLog log) {
        userLogTable.putItem(log);
    }

    @PostConstruct
    public void createTableIfNotExists() {
        try {
            userLogTable.createTable();
        } catch (Exception e) {
            System.out.println("UserLog table may already exist or creation failed: " + e.getMessage());
        }
    }

    public List<UserLog> scanAll(int pageSize, Map<String, AttributeValue> startKey,
                                 List<UserLog> results, Holder<Map<String, AttributeValue>> lastKeyHolder) {
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<UserLog> pages = userLogTable.scan(scanRequest);
        List<Page<UserLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());

        if (!pageList.isEmpty()) {
            Page<UserLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public List<UserLog> queryWithFilters(Map<String, String> filters,
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

        PageIterable<UserLog> pages = userLogTable.query(queryRequest);
        List<UserLog> results = new ArrayList<>();

        List<Page<UserLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());
        if (!pageList.isEmpty()) {
            Page<UserLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public static class Holder<T> {
        public T value;
    }
}
