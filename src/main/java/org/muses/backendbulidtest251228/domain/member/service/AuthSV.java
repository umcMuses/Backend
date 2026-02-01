package org.muses.backendbulidtest251228.domain.member.service;

import static org.muses.backendbulidtest251228.domain.mypage.service.MyPageSRVI.*;

import java.util.Map;
import java.util.Set;

import org.muses.backendbulidtest251228.domain.member.dto.AuthRequestDT;
import org.muses.backendbulidtest251228.domain.member.dto.AuthResponseDT;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.enums.Provider;
import org.muses.backendbulidtest251228.domain.member.enums.Role;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.domain.storage.entity.AttachmentENT;
import org.muses.backendbulidtest251228.domain.storage.service.AttachmentSRV;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.muses.backendbulidtest251228.global.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthSV {
	private final MemberRepo memberRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final AttachmentSRV attachmentSRV;

	private static final String PROFILE_TARGET_TYPE = "member";
	private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp");

	// 자체 회원가입
	@Transactional
	public Long localSignup(AuthRequestDT.LocalSignupRequest request) {
		if(memberRepo.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("이미 가입된 이메일입니다.");
		}
		Member member = Member.builder()
			.name(request.getName())
			.email(request.getEmail())
			.phoneNumber(request.getPhoneNumber())
			.passwd(passwordEncoder.encode(request.getPassword()))
			.role(Role.GUEST)
			.provider(Provider.LOCAL)
			.build();

		return memberRepo.save(member).getId();
	}
	// 자체 로그인
	public AuthResponseDT.TokenResponse login(AuthRequestDT.LocalLoginRequest request) {
		Member member = memberRepo.findByEmail(request.getEmail())
			.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

		if (!passwordEncoder.matches(request.getPassword(), member.getPasswd())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
		String refreshToken = "";

		return new AuthResponseDT.TokenResponse(accessToken, refreshToken, member.getRole(), member.getName());
	}
	// 소셜 로그인
	@Transactional
	public Member socialLoginProcess(String email, String name, Provider provider, String providerId) {

		Member member = memberRepo.findByEmail(email).orElse(null);

		if(member == null) {
			member = Member.builder()
				.email(email)
				.name(name)
				.provider(provider)
				.providerId(providerId)
				.role(Role.GUEST)
				.build();
			return memberRepo.save(member);
		}
		// 기존 회원 반환
		return member;
	}

	// 프로필 설정 (회원가입 후) - 이미지 업로드 포함
	@Transactional
	public AuthResponseDT.TokenResponse setupProfile(String email, AuthRequestDT.ProfileSetupRequest request, MultipartFile profileImg) {
		Member member = memberRepo.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		// 닉네임 중복 확인
		if (memberRepo.existsByNickName(request.getNickName())) {
			throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
		}
		String profileImgUrl = null;
		if (profileImg != null && !profileImg.isEmpty()) {
			validateImageExtension(profileImg);
			// 기존 프로필 이미지 삭제
			attachmentSRV.deleteAll(PROFILE_TARGET_TYPE, member.getId());
			// 새 이미지 업로드
			AttachmentENT saved = attachmentSRV.upload(PROFILE_TARGET_TYPE, member.getId(), profileImg);
			profileImgUrl = saved.getFileUrl();
		}
		// 업데이트, role 변경
		member.completeSignup(
			profileImgUrl,
			request.getNickName(),
			request.getIntroduction(),
			request.getBirthday(),
			Integer.valueOf(request.getGender())
		);
		String newAccessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
		return new AuthResponseDT.TokenResponse(newAccessToken, "", member.getRole(), member.getName());
	}

	// 중복 확인 메서드
	public boolean checkEmailDuplicate(String email) {
		return memberRepo.existsByEmail(email);
	}
	public boolean checkNickNameDuplicate(String nickName) {
		return memberRepo.existsByNickName(nickName);
	}

	@Transactional
	public void withdraw(String email) {
		Member member = memberRepo.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		// 프로필 이미지 삭제
		attachmentSRV.deleteAll(PROFILE_TARGET_TYPE, member.getId());
		// MVP 이므로 일단 즉시 삭제만 구현
		memberRepo.delete(member);
	}

	// 이미지 확장자 검증
	private void validateImageExtension(MultipartFile file) {
		String original = file.getOriginalFilename();
		if (original == null || !original.contains(".")) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "파일 확장자가 필요합니다.");
		}
		String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();

		if (!ALLOWED_EXT.contains(ext)) {
			throw new BusinessException(
				ErrorCode.BAD_REQUEST,
				"허용되지 않는 이미지 확장자입니다.",
				Map.of("allowed", ALLOWED_EXT, "extension", ext)
			);
		}
	}
}
