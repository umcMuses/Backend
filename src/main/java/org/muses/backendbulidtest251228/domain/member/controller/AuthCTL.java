package org.muses.backendbulidtest251228.domain.member.controller;

import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.domain.member.dto.AuthRequestDT;
import org.muses.backendbulidtest251228.domain.member.dto.AuthResponseDT;
import org.muses.backendbulidtest251228.domain.member.service.AuthSV;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthCTL {

	private final AuthSV authSV;

	@PostMapping("/signup")
	public ApiResponse<Long> signup(
			@RequestBody AuthRequestDT.LocalSignupRequest request) {

		Long memberId = authSV.localSignup(request);
		return ApiResponse.success(memberId);
	}

	@PostMapping("/login")
	public ApiResponse<AuthResponseDT.TokenResponse> login(
			@RequestBody AuthRequestDT.LocalLoginRequest request) {

		AuthResponseDT.TokenResponse response = authSV.login(request);
		return ApiResponse.success(response);
	}

	@PostMapping("/profile/create")
	public ApiResponse<AuthResponseDT.TokenResponse> setupProfile(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestBody AuthRequestDT.ProfileSetupRequest request) {

		AuthResponseDT.TokenResponse response = authSV.setupProfile(userDetails.getUsername(), request);
		return ApiResponse.success(response);
	}

	// 중복 확인
	@GetMapping("/signup/check-email")
	public ApiResponse<Boolean> checkEmailDuplicate(@RequestParam String email) {
		return ApiResponse.success(authSV.checkEmailDuplicate(email));
	}
	@GetMapping("/profile/check-nickname")
	public ApiResponse<Boolean> checkNickName(@RequestParam String nickName) {
		return ApiResponse.success(authSV.checkNickNameDuplicate(nickName));
	}

	@PostMapping("/logout")
	public ApiResponse<String> logout(
			@AuthenticationPrincipal UserDetails userDetails) {
		// 추후 구현 예정
		return ApiResponse.success("로그아웃 되었습니다.");
	}

	@DeleteMapping("/withdraw")
	public ApiResponse<String> withdraw(
			@AuthenticationPrincipal UserDetails userDetails) {
		// 추후 추가 구현 예정
		authSV.withdraw(userDetails.getUsername());
		return ApiResponse.success("회원탈퇴가 완료되었습니다.");
	}

}
