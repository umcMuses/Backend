package org.muses.backendbulidtest251228.domain.mypage.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.mypage.dto.MyProfileResponse;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateMyProfileRequest;
import org.muses.backendbulidtest251228.domain.mypage.dto.UpdateProfileImageResponse;
import org.muses.backendbulidtest251228.domain.mypage.repository.MyPageMemberRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.muses.backendbulidtest251228.common.ApiException;
import org.muses.backendbulidtest251228.common.ErrorCode;
// 여기는 회원 로직 완성되면 추후에 수정
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MyPageMemberRepository memberRepository;

    @Override
    public MyProfileResponse getMe() {
        Member m = getCurrentMember();
        return toResponse(m);
    }

    @Override
    @Transactional
    public MyProfileResponse updateProfile(UpdateMyProfileRequest request) {
        Member m = getCurrentMember();

        // 닉네임 중복 체크 (본인 닉네임이면 통과)
        if (request.getNickName() != null
                && !m.getNickName().equals(request.getNickName())
                && memberRepository.existsByNickName(request.getNickName())) {
            // 400으로 떨어지게
            throw new ApiException(ErrorCode.DUPLICATE, "이미 사용 중인 닉네임입니다.",
                    Map.of("nickName", request.getNickName()));
        }

        // gender 값 검증 (0/1만)
        if (request.getGender() != null && request.getGender() != 0 && request.getGender() != 1) {
            // 400으로 떨어지게
            throw new ApiException(ErrorCode.BAD_REQUEST, "gender는 0(남자) 또는 1(여자)만 가능합니다.",
                    Map.of("gender", request.getGender()));
        }

        // Member 엔티티에 메서드 1개 추가 필요 (changeProfile)
        m.changeProfile(
                request.getNickName(),
                request.getIntroduction(),
                request.getBirthday(),
                request.getGender()
        );

        return toResponse(m);
    }

    @Override
    @Transactional
    public UpdateProfileImageResponse updateProfileImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "이미지 파일이 필요합니다.");
        }

        Member m = getCurrentMember();

        // 최소 구현: 로컬 uploads 폴더에 저장
        String url = saveLocal(image);

        // Member 엔티티에 메서드 1개 추가 필요 (changeProfileImage)
        m.changeProfileImage(url);

        return UpdateProfileImageResponse.builder()
                .profileImgUrl(url)
                .build();
    }

    private MyProfileResponse toResponse(Member m) {
        return MyProfileResponse.builder()
                .memberId(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .nickName(m.getNickName())
                .introduction(m.getIntroduction())
                .birthday(m.getBirthday())
                .gender(m.getGender())
                .profileImgUrl(m.getProfileImgUrl())
                .ticketCount(m.getTicketCount())
                .supportCount(m.getSupportCount())
                .supportLevel(m.getSupportLevel())
                .build();
    }

    private Member getCurrentMember() {
        Long memberId = resolveCurrentMemberId();
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.NOT_FOUND,
                        "회원 정보를 찾을 수 없습니다.",
                        Map.of("memberId", memberId)
                ));
    }

    /**
     * - (임시) Postman 테스트: X-TEST-MEMBER-ID 헤더가 있으면 그걸로 memberId 결정
     * - (정식) Spring Security 인증 기반
     * 반환 실패 시:
     * - 인증이 없으면 401 (AUTH_REQUIRED)
     * - principal 파싱 불가면 401 (AUTH_INVALID)
     */
    private Long resolveCurrentMemberId() {

        // 1) 임시: Postman 테스트용 헤더가 있으면 그걸로 memberId 결정
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest req = attrs.getRequest();
            String testId = req.getHeader("X-TEST-MEMBER-ID");
            if (testId != null && !testId.isBlank()) {
                try {
                    return Long.parseLong(testId.trim());
                } catch (NumberFormatException e) {
                    throw new ApiException(
                            ErrorCode.BAD_REQUEST,
                            "X-TEST-MEMBER-ID는 숫자여야 합니다.",
                            Map.of("header", "X-TEST-MEMBER-ID", "value", testId)
                    );
                }
            }
        }

        // 2) 원래 로직: Spring Security 인증 기반
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            // 401로 떨어지게
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }

        Object principal = auth.getPrincipal();
        if (principal == null) {
            throw new ApiException(ErrorCode.AUTH_REQUIRED);
        }

        if (principal instanceof Long l) return l;
        if (principal instanceof Integer i) return i.longValue();

        Long id = tryInvokeLongGetter(principal, "getId");
        if (id != null) return id;

        id = tryInvokeLongGetter(principal, "getMemberId");
        if (id != null) return id;

        // 지금은 클라이언트 입장에서 로그인/토큰 문제로 보이게 401(AUTH_INVALID)로 통일.
        throw new ApiException(
                ErrorCode.AUTH_INVALID,
                "principal에서 memberId를 찾을 수 없습니다.",
                Map.of("principalClass", principal.getClass().getName())
        );
    }

    private Long tryInvokeLongGetter(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            Object v = m.invoke(target);
            if (v instanceof Long l) return l;
            if (v instanceof Integer i) return i.longValue();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String saveLocal(MultipartFile file) {
        try {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }

            String savedName = UUID.randomUUID() + ext;
            Path target = uploadDir.resolve(savedName);
            file.transferTo(target.toFile());

            // /uploads/{filename}로 접근 가능하게 할 계획이면 정적 리소스 매핑 필요
            return "/uploads/" + savedName;

        } catch (Exception e) {
            // 서버 에러로 떨어지게
            throw new ApiException(
                    ErrorCode.SERVER_ERROR,
                    "이미지 저장 실패",
                    Map.of("originalMessage", e.getMessage())
            );
        }
    }
}

