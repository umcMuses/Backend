package org.muses.backendbulidtest251228.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthRedirectCTL {
	@GetMapping("/auth/callback")
	public ResponseEntity<String> handleCallback() {
		return ResponseEntity.ok("인증 콜백 페이지입니다. URL의 토큰을 확인하세요.");
	}
}
