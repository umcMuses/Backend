package org.muses.backendbulidtest251228.domain.event.entity;

import java.time.LocalDateTime;

import org.muses.backendbulidtest251228.domain.event.enums.EventCategory;
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
@Table(name = "events")
public class Event extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private LocalDateTime uploadDateTime;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EventCategory category;

	public void update(String title, String description, String content, LocalDateTime uploadDateTime, EventCategory category) {
		this.title = title;
		this.description = description;
		this.content = content;
		this.uploadDateTime = uploadDateTime;
		this.category = category;
	}

}
