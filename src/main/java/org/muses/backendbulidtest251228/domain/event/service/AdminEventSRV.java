package org.muses.backendbulidtest251228.domain.event.service;

import java.time.LocalDateTime;

import org.muses.backendbulidtest251228.domain.event.dto.EventDT;
import org.muses.backendbulidtest251228.domain.event.entity.Event;
import org.muses.backendbulidtest251228.domain.event.repository.EventRepo;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventSRV {

	private final EventRepo eventRepo;

	public Long createEvent(EventDT.EventRequest request) {
		// 예약 시간이 있으면(not null) 그 시간으로, 없으면(null) 현재 시간으로
		LocalDateTime scheduledTime = request.getUploadDateTime() != null
			? request.getUploadDateTime() : LocalDateTime.now();

		Event event = Event.builder()
			.category(request.getCategory())
			.title(request.getTitle())
			.description(request.getDescription())
			.content(request.getContent())
			.uploadDateTime(scheduledTime)
			.build();

		return eventRepo.save(event).getId();
	}

	public void updateEvent(Long eventId, EventDT.EventRequest request) {
		Event event = eventRepo.findById(eventId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		LocalDateTime newUploadDateTime = request.getUploadDateTime() != null
			? request.getUploadDateTime() : event.getUploadDateTime();

		event.update(
			request.getTitle(),
			request.getDescription(),
			request.getContent(),
			newUploadDateTime,
			request.getCategory()
		);
	}

	public void deleteEvent(Long eventId) {
		Event event = eventRepo.findById(eventId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		eventRepo.delete(event);
	}

	@Transactional(readOnly = true)
	public EventDT.EventDetailResponse getEventDetail(Long eventId) {
		Event event = eventRepo.findById(eventId)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
		return EventDT.EventDetailResponse.from(event);
	}

	@Transactional(readOnly = true)
	public Page<EventDT.EventListResponse> getAllEvents(Pageable pageable) {
		return eventRepo.findAll(pageable)
			.map(EventDT.EventListResponse::from);
	}
}
