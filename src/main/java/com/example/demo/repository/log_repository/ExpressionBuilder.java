package com.example.demo.repository.log_repository;

import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class ExpressionBuilder {
    public static Expression buildExpression(Map<String, String> filters) {
        Map<String, String> expressionNames = new HashMap<>();
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        StringBuilder expression = new StringBuilder();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String field = entry.getKey();
            String placeholderName = "#" + field;
            String placeholderValue = ":" + field;

            expressionNames.put(placeholderName, field);
            expressionValues.put(placeholderValue, AttributeValue.builder().s(entry.getValue()).build());

            if (expression.length() > 0) {
                expression.append(" AND ");
            }
            expression.append(placeholderName).append(" = ").append(placeholderValue);
        }

        return Expression.builder()
                .expression(expression.toString())
                .expressionNames(expressionNames)
                .expressionValues(expressionValues)
                .build();
    }
}
