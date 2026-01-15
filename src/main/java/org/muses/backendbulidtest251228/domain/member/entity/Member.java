package org.muses.backendbulidtest251228.domain.member.entity;

import org.hibernate.annotations.ColumnDefault;
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
	@Column(length = 20)
	private String phoneNumber;
	@Column(name = "profile_img_url")
	private String profileImgUrl;
	@Column(nullable = false, unique = true)
	private String nickName;
	@Column(nullable = false, length = 500)
	private String introduction;
	@Column(nullable = false)
	private String birthday;
	@ColumnDefault("0")
	@Column(nullable = false)
	private Integer gender = 0;

	@ColumnDefault("0")
	@Column(name = "ticket_count", nullable = false)
	private Integer ticketCount = 0;
	@ColumnDefault("0")
	@Column(name = "support_count", nullable = false)
	private Integer supportCount = 0;
	@ColumnDefault("1")
	@Column(name = "support_level", nullable = false)
	private Integer supportLevel = 1;
}
