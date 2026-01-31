package org.muses.backendbulidtest251228.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class S3Config {

	private final AwsProperties awsProperties;

	@Bean
	public S3Client s3Client() {
		AwsBasicCredentials credentials = AwsBasicCredentials.create(
			awsProperties.getAccessKey(),
			awsProperties.getSecretKey()
		);

		return S3Client.builder()
			.region(Region.of(awsProperties.getRegion()))
			.credentialsProvider(StaticCredentialsProvider.create(credentials))
			.build();
	}
}
