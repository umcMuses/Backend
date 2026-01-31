package org.muses.backendbulidtest251228.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@Profile("prod")
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
	private String accessKey;
	private String secretKey;
	private String region;
	private S3 s3 = new S3();

	@Getter
	@Setter
	public static class S3 {
		private String bucket;
	}
}
