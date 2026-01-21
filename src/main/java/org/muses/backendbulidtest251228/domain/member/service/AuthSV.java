package org.muses.backendbulidtest251228.domain.member.service;

import org.muses.backendbulidtest251228.domain.member.dto.AuthRequestDT;
import org.muses.backendbulidtest251228.domain.member.dto.AuthResponseDT;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.member.entity.Provider;
import org.muses.backendbulidtest251228.domain.member.entity.Role;
import org.muses.backendbulidtest251228.domain.member.repository.MemberRepo;
import org.muses.backendbulidtest251228.global.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthSV {
	private final MemberRepo memberRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

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
	public AuthResponseDT.TokenResponse socialLoginProcess(String email, String name, Provider provider, String providerId) {

		Member member = memberRepo.findByEmail(email).orElse(null);

		if(member == null) {
			member = Member.builder()
				.email(email)
				.name(name)
				.provider(provider)
				.providerId(providerId)
				.role(Role.GUEST)
				.build();
			memberRepo.save(member);
		}
		// 토큰 발급
		String accessToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().name());
		return new AuthResponseDT.TokenResponse(accessToken, "", member.getRole(), member.getName());
	}

	// 프로필 설정 (회원가입 후)
	@Transactional
	public AuthResponseDT.TokenResponse setupProfile(String email, AuthRequestDT.ProfileSetupRequest request) {
		Member member = memberRepo.findByEmail(email)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
		// 닉네임 중복 확인
		if (memberRepo.existsByNickName(request.getNickName())) {
			throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
		}
		// 업데이트, role 변경
		member.completeSignup(
			request.getProfileImgUrl(),
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
		// MVP 이므로 일단 즉시 삭제만 구현
		memberRepo.delete(member);
	}
}
