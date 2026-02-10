package org.muses.backendbulidtest251228.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
		@NotBlank
		private String nickName;
		private String introduction;
		@NotBlank @Schema(description = "생년월일 (yyyy-mm-dd)", example = "2001-01-01")
		private String birthday;
		@NotNull @Min(0) @Max(1)
		private Integer gender;
	}
}
