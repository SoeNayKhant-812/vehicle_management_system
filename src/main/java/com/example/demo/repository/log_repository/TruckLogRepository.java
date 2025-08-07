package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.TruckLog;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TruckLogRepository {

    private final DynamoDbTable<TruckLog> truckLogTable;

    public TruckLogRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.truckLogTable = enhancedClient.table("truck_log", TableSchema.fromBean(TruckLog.class));
    }

    public void save(TruckLog log) {
        truckLogTable.putItem(log);
    }

    @PostConstruct
    public void createTableIfNotExists() {
        try {
            truckLogTable.createTable();
        } catch (Exception e) {
            System.out.println("truck_log table may already exist or creation failed: " + e.getMessage());
        }
    }

    public List<TruckLog> scanAll(int pageSize,
                                  Map<String, AttributeValue> startKey,
                                  List<TruckLog> results,
                                  Holder<Map<String, AttributeValue>> lastKeyHolder) {
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<TruckLog> pages = truckLogTable.scan(scanRequest);
        List<Page<TruckLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());

        if (!pageList.isEmpty()) {
            Page<TruckLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public List<TruckLog> queryWithFilters(Map<String, String> filters,
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

        PageIterable<TruckLog> pages = truckLogTable.query(queryRequest);
        List<TruckLog> results = new ArrayList<>();

        List<Page<TruckLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());
        if (!pageList.isEmpty()) {
            Page<TruckLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public static class Holder<T> {
        public T value;
    }
}
