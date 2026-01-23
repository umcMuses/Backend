package org.muses.backendbulidtest251228.global.config;

import java.util.Arrays;

import org.muses.backendbulidtest251228.domain.member.service.CustomOAuth2UserSV;
import org.muses.backendbulidtest251228.global.jwt.JwtAuthenticationFilter;
import org.muses.backendbulidtest251228.global.jwt.JwtTokenProvider;
import org.muses.backendbulidtest251228.global.security.HttpCookieOAuth2AuthorRequestRepo;
import org.muses.backendbulidtest251228.global.security.handler.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomOAuth2UserSV customOAuth2UserSV;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final HttpCookieOAuth2AuthorRequestRepo httpCookieOAuth2AuthorizationRequestRepository;


	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			// basic auth, csrf 보안 사용 X
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			// JWT 사용 -> 세션 사용 X
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// Swagger 허용
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/api/auth/profile/**").authenticated()
				.requestMatchers("/api/auth/**", "/oauth2/**", "/api/projects/**", "/api/alarms/**", "/api/events/**", "/health", "/error").permitAll()
				.requestMatchers("/api/creators/**").hasRole("CREATOR")
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			// JWT 필터 등록
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
			// OAuth2 로그인 설정
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.baseUri("/oauth2/authorization")
					.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
				)
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserSV)
				)
				.successHandler(oAuth2LoginSuccessHandler)
			);

		return http.build();
	}
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(Arrays.asList(
			"http://localhost:3000",
			"http://localhost:5173",
			"http://localhost:8080",
			"http://localhost:9095",
			"http://localhost:9098"
		));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList("Authorization"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
