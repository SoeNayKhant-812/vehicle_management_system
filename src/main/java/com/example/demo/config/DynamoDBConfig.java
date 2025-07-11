package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.regions.Region;
import java.net.URI;

@Configuration
@EnableConfigurationProperties(DynamoDBProperties.class)
public class DynamoDBConfig {

	private final DynamoDBProperties props;

	public DynamoDBConfig(DynamoDBProperties props) {
		this.props = props;
	}

	@Bean
	public DynamoDbClient dynamoDbClient() {
		return DynamoDbClient.builder().region(Region.of(props.getRegion()))
				.credentialsProvider(StaticCredentialsProvider
						.create(AwsBasicCredentials.create(props.getAccessKey(), props.getSecretKey())))
				.endpointOverride(URI.create(props.getEndpoint())).build();
	}
}
