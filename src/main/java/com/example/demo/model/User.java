package com.example.demo.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.io.Serializable;
import java.time.Instant;

@DynamoDbBean
public class User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String USERNAME_INDEX = "username-index";
	
	private String id;
	private String username;
	private String password;
	private String email;
	private Instant createdAt;
	private Role role;
	private Instant tokenValidAfter;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@DynamoDbSecondaryPartitionKey(indexNames = USERNAME_INDEX)
	@DynamoDbAttribute("username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@DynamoDbAttribute("password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@DynamoDbAttribute("email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@DynamoDbAttribute("createdAt")
	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	@DynamoDbAttribute("role")
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	@DynamoDbAttribute("tokenValidAfter")
	public Instant getTokenValidAfter() {
		return tokenValidAfter;
	}

	public void setTokenValidAfter(Instant tokenValidAfter) {
		this.tokenValidAfter = tokenValidAfter;
	}
}
