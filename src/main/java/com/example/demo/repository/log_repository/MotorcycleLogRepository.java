package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.MotorcycleLog;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MotorcycleLogRepository {

    private final DynamoDbTable<MotorcycleLog> motorcycleLogTable;

    public MotorcycleLogRepository(DynamoDbClient client) {
        DynamoDbEnhancedClient enhanced = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(client)
                .build();
        this.motorcycleLogTable = enhanced.table("motorcycle_log", TableSchema.fromBean(MotorcycleLog.class));
    }

    @PostConstruct
    public void init() {
        try {
            motorcycleLogTable.createTable();
        } catch (Exception e) {
            System.out.println("motorcycle_log table may already exist or creation failed: " + e.getMessage());
        }
    }

    public void save(MotorcycleLog log) {
        motorcycleLogTable.putItem(log);
    }

    public List<MotorcycleLog> scanAll(int pageSize,
                                       Map<String, AttributeValue> startKey,
                                       List<MotorcycleLog> results,
                                       Holder<Map<String, AttributeValue>> lastKeyHolder) {
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<MotorcycleLog> pages = motorcycleLogTable.scan(scanRequest);
        List<Page<MotorcycleLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());

        if (!pageList.isEmpty()) {
            Page<MotorcycleLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public List<MotorcycleLog> queryWithFilters(Map<String, String> filters,
                                                Map<String, AttributeValue> startKey,
                                                int pageSize,
                                                Holder<Map<String, AttributeValue>> lastKeyHolder) {
        Expression expression = ExpressionBuilder.buildExpression(filters);

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                        .partitionValue(filters.get("id")) // assumes "id" is the hash key
                        .build()))
                .filterExpression(expression)
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<MotorcycleLog> pages = motorcycleLogTable.query(queryRequest);
        List<MotorcycleLog> results = new ArrayList<>();

        List<Page<MotorcycleLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());
        if (!pageList.isEmpty()) {
            Page<MotorcycleLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public static class Holder<T> {
        public T value;
    }
}
