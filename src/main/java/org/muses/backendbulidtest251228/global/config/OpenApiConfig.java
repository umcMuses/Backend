package org.muses.backendbulidtest251228.global.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
    @Bean(name = "serverConfig")
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.servers(Arrays.asList(
				new Server().url("https://mymuses.site").description("Production Server"),
				new Server().url("http://localhost:8080").description("Local Server")
			));
	}
}
