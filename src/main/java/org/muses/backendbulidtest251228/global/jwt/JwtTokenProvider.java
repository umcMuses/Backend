package org.muses.backendbulidtest251228.global.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.muses.backendbulidtest251228.global.security.PrincipalDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	@Value("${jwt.secret}")
	private String secretKeyPlain;
	private SecretKey key;

	// Access Token 유효시간 (예: 1시간)
	private final long ACCESS_TOKEN_VALIDITY = 1000L * 60 * 60;

	private final PrincipalDetailsService principalDetailsService;

	@PostConstruct
	protected void init() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKeyPlain);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	// 토큰 생성
	public String createToken(String email, String role) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

		return Jwts.builder()
			.subject(email)
			.claim("role", role)
			.issuedAt(now)
			.expiration(validity)
			.signWith(key)
			.compact();
	}

	// 토큰에서 인증 정보(Authentication) 조회
	public Authentication getAuthentication(String token) {
		UserDetails userDetails = principalDetailsService.loadUserByUsername(this.getUserEmail(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	// 토큰에서 회원 이메일 추출
	public String getUserEmail(String token) {
		return Jwts.parser()
			.verifyWith(key)
			.build()
			.parseSignedClaims(token)
			.getPayload()
			.getSubject();
	}

	// 토큰 유효성 검증
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("invalid JWT 서명입니다.");
		} catch (ExpiredJwtException e) {
			log.info("만료된 JWT 토큰입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.info("잘못된 JWT 토큰입니다.");
		}
		return false;
	}
}
