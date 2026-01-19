package org.muses.backendbulidtest251228.global.config;

import org.muses.backendbulidtest251228.global.jwt.JwtAuthenticationFilter;
import org.muses.backendbulidtest251228.global.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtTokenProvider jwtTokenProvider;

	// Local 회원가입 시 비밀번호 암호화
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			// basic auth, csrf 보안 사용 X
			.httpBasic(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			// JWT 사용 -> 세션 사용 X
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// 전체 접근 가능 페이지
				.requestMatchers("/api/auth/**", "/api/projects", "/api/events/**", "/health", "/error").permitAll()
				// 크리에이터 페이지
				.requestMatchers("/api/creators/**").hasRole("CREATOR")
				// 관리자 페이지
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				// 그 외 모든 요청
				.anyRequest().authenticated()
			)
			// JWT 필터 등록
			.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}
