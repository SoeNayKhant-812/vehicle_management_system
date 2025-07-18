package com.example.demo.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class Role {

	private String name;
	private String description;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.toUpperCase();
	}

	@DynamoDbAttribute("description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
