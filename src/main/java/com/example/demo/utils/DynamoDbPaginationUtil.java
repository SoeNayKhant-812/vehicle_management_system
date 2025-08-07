package com.example.demo.utils;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class DynamoDbPaginationUtil {
	public static Map<String, AttributeValue> decodeStartKey(Map<String, String> encoded) {
		if (encoded == null || encoded.isEmpty())
			return null;
		Map<String, AttributeValue> decoded = new HashMap<>();
		for (Map.Entry<String, String> entry : encoded.entrySet()) {
			decoded.put(entry.getKey(), AttributeValue.builder().s(entry.getValue()).build());
		}
		return decoded;
	}
}
