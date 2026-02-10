package org.muses.backendbulidtest251228.domain.member.controller;

import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.domain.member.dto.AuthRequestDT;
import org.muses.backendbulidtest251228.domain.member.dto.AuthResponseDT;
import org.muses.backendbulidtest251228.domain.member.service.AuthSV;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name= "회원가입/로그인 관련 API", description= "사용자 자체 로그인 및 회원가입, 초기 프로필 생성")
public class AuthCTL {

	private final AuthSV authSV;

	@Operation(summary = "자체 회원가입 API", description = "회원가입 시 필요한 사용자의 정보들을 전달")
	@PostMapping("/signup")
	public ApiResponse<Long> signup(
			@RequestBody AuthRequestDT.LocalSignupRequest request) {

		Long memberId = authSV.localSignup(request);
		return ApiResponse.success(memberId);
	}

	@Operation(summary = "자체 로그인 API", description = "사용자가 입력한 이메일과 비밀번호로 로그인합니다.")
	@PostMapping("/login")
	public ApiResponse<AuthResponseDT.TokenResponse> login(
			@RequestBody AuthRequestDT.LocalLoginRequest request) {

		AuthResponseDT.TokenResponse response = authSV.login(request);
		return ApiResponse.success(response);
	}

	@Operation(summary = "회원가입 직후 초기 프로필 생성 API",
		description =
			"<p>자체/소셜 회원가입 직후 이어지는 API이며, 회원가입을 했지만 프로필을 설정하지 않고 종료했을 경우 여기로 리다이렉트 됩니다.</p>"
				+ "<p>(프로필 이미지는 선택 사항입니다.)</p>")
	@PostMapping(value = "/profile/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ApiResponse<AuthResponseDT.TokenResponse> setupProfile(
			@AuthenticationPrincipal UserDetails userDetails,
			@Parameter(description = "프로필 이미지 (선택, jpg/jpeg/png/webp)")
			@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
			@Parameter(description = "닉네임")
			@RequestPart("nickName") String nickName,
			@Parameter(description = "자기소개")
			@RequestPart(value = "introduction", required = false) String introduction,
			@Parameter(description = "생년월일 (yyyy-MM-dd)")
			@RequestPart(value = "birthday") String birthday,
			@Parameter(description = "성별 (0=남, 1=여)")
			@RequestParam(value = "gender") Integer gender
	) {
		AuthRequestDT.ProfileSetupRequest request = new AuthRequestDT.ProfileSetupRequest();
		request.setNickName(nickName);
		request.setIntroduction(introduction);
		request.setBirthday(birthday);
		request.setGender(gender);

		AuthResponseDT.TokenResponse response = authSV.setupProfile(userDetails.getUsername(), request, profileImage);
		return ApiResponse.success(response);
	}

	// 중복 확인
	@Operation(summary = "이메일 중복 확인 API", description = "자체 회원가입에서 이메일 중복 확인할 때 사용합니다.")
	@GetMapping("/signup/check-email")
	public ApiResponse<Boolean> checkEmailDuplicate(@RequestParam String email) {
		return ApiResponse.success(authSV.checkEmailDuplicate(email));
	}

	@Operation(summary = "닉네임 중복 확인 API", description = "프로필 설정에서 닉네임 중복 확인을 할 때 사용합니다.")
	@GetMapping("/profile/check-nickname")
	public ApiResponse<Boolean> checkNickName(@RequestParam String nickName) {
		return ApiResponse.success(authSV.checkNickNameDuplicate(nickName));
	}

	@Operation(summary = "로그아웃 API")
	@PostMapping("/logout")
	public ApiResponse<String> logout(
			@AuthenticationPrincipal UserDetails userDetails) {
		// 추후 구현 예정
		return ApiResponse.success("로그아웃 되었습니다.");
	}

	@Operation(summary = "회원 탈퇴 API", description = "해당 API 호출 시 바로 회원이 삭제됩니다.")
	@DeleteMapping("/withdraw")
	public ApiResponse<String> withdraw(
			@AuthenticationPrincipal UserDetails userDetails) {
		// 추후 추가 구현 예정
		authSV.withdraw(userDetails.getUsername());
		return ApiResponse.success("회원탈퇴가 완료되었습니다.");
	}

}
