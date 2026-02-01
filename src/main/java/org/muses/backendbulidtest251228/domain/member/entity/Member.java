package org.muses.backendbulidtest251228.domain.member.entity;

import org.hibernate.annotations.ColumnDefault;
import org.muses.backendbulidtest251228.domain.member.enums.Provider;
import org.muses.backendbulidtest251228.domain.member.enums.Role;
import org.muses.backendbulidtest251228.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Provider provider;
	@Column(name = "provider_id")
	private String providerId;
	private String passwd;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;
	@Column(nullable = false, length = 50)
	private String name;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(name = "phone_number", length = 20)
	private String phoneNumber;
	// -- 프로필 설정 --
	@Column(name = "profile_img_url")
	private String profileImgUrl;
	@Column(name = "nick_name", unique = true)
	private String nickName;
	@Column(length = 500)
	private String introduction;
	private String birthday;
	private Integer gender;

	@Builder.Default
	@ColumnDefault("0")
	@Column(name = "ticket_count", nullable = false)
	private Integer ticketCount = 0;
	@Builder.Default
	@ColumnDefault("0")
	@Column(name = "support_count", nullable = false)
	private Integer supportCount = 0;
	@Builder.Default
	@ColumnDefault("1")
	@Column(name = "support_level", nullable = false)
	private Integer supportLevel = 1;

	// -- 생성자 --
	@Builder
	public Member(String email, String passwd, String name, String phoneNumber, String providerId, Role role) {
		this.email = email;
		this.passwd = passwd;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.providerId = providerId;
		this.role = role;
	}

	public void completeSignup(String profileImgUrl, String nickName, String introduction, String birthday, Integer gender) {
		this.profileImgUrl = profileImgUrl;
		this.nickName = nickName;
		this.introduction = introduction;
		this.birthday = birthday;
		this.gender = gender;
		this.role = Role.MAKER;	// GUEST -> MAKER
	}

	public void changeProfile(String nickName, String introduction, String birthday, Integer gender) {
		this.nickName = nickName;
		this.introduction = introduction;
		this.birthday = birthday;
		if (gender != null) this.gender = gender;
	}

    public void changeProfileImage(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

	/**
	 * 회원 Role 변경 (관리자 크리에이터 전환 승인 시 사용)
	 * @param newRole 새로운 Role
	 */
	public void changeRole(Role newRole) {
		this.role = newRole;
	}

	/**
	 * 크리에이터로 승격
	 */
	public void upgradeToCreator() {
		this.role = Role.CREATOR;
	}
}
