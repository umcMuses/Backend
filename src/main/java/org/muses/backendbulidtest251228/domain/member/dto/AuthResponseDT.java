package org.muses.backendbulidtest251228.domain.member.dto;

import org.muses.backendbulidtest251228.domain.member.enums.Role;

import lombok.Data;

public class AuthResponseDT {

	@Data
	public static class TokenResponse {
		private String accessToken;
		private String refreshToken;
		private Role role;
		private String name;

		public TokenResponse(String accessToken, String refreshToken, Role role, String name) {
			this.accessToken = accessToken;
			this.refreshToken = refreshToken;
			this.role = role;
			this.name = name;
		}
	}
}
