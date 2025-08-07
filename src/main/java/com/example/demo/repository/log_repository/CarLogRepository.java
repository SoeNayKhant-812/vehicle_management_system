package com.example.demo.repository.log_repository;

import com.example.demo.model.log_model.CarLog;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CarLogRepository {

    private final DynamoDbTable<CarLog> carLogTable;

    public CarLogRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.carLogTable = enhancedClient.table("CarLog", TableSchema.fromBean(CarLog.class));
    }

    public void save(CarLog log) {
        carLogTable.putItem(log);
    }
    
	@PostConstruct
	public void createTableIfNotExists() {
		try {
			carLogTable.createTable();
		} catch (Exception e) {
			System.out.println("CarLog table may already exist or creation failed: " + e.getMessage());
		}
	}

    public List<CarLog> scanAll(int pageSize, Map<String, AttributeValue> startKey, 
                                List<CarLog> results, Holder<Map<String, AttributeValue>> lastKeyHolder) {
        ScanEnhancedRequest scanRequest = ScanEnhancedRequest.builder()
                .limit(pageSize)
                .exclusiveStartKey(startKey)
                .build();

        PageIterable<CarLog> pages = carLogTable.scan(scanRequest);
        List<Page<CarLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());

        if (!pageList.isEmpty()) {
            Page<CarLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public List<CarLog> queryWithFilters(Map<String, String> filters,
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

        PageIterable<CarLog> pages = carLogTable.query(queryRequest);
        List<CarLog> results = new ArrayList<>();

        List<Page<CarLog>> pageList = pages.stream().limit(1).collect(Collectors.toList());
        if (!pageList.isEmpty()) {
            Page<CarLog> page = pageList.get(0);
            results.addAll(page.items());
            lastKeyHolder.value = page.lastEvaluatedKey();
        }

        return results;
    }

    public static class Holder<T> {
        public T value;
    }
}