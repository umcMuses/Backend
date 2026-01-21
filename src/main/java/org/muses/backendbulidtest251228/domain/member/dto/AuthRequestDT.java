package org.muses.backendbulidtest251228.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Getter
public class AuthRequestDT {

	@Data
	public static class LocalSignupRequest {
		@NotBlank
		private String name;
		@Email @NotBlank
		private String email;
		@NotBlank
		private String phoneNumber;
		@NotBlank
		private String password;
	}

	@Data
	public static class LocalLoginRequest {
		@Email @NotBlank
		private String email;
		@NotBlank
		private String password;
	}

	@Data
	public static class ProfileSetupRequest {
		private String profileImgUrl;
		private String nickName;
		private String introduction;
		private String birthday;
		private String gender;
	}
}
