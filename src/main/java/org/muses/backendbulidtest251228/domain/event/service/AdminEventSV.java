package org.muses.backendbulidtest251228.domain.event.service;

import org.muses.backendbulidtest251228.domain.event.dto.EventDT;
import org.muses.backendbulidtest251228.domain.event.entity.Event;
import org.muses.backendbulidtest251228.domain.event.repository.EventRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventSV {

	private final EventRepo eventRepo;

	public Long createEvent(EventDT.EventRequest request) {
		// TODO
		return null;
	}

	public void updateEvent(Long id, EventDT.EventRequest request) {
		// TODO
	}

	public void deleteEvent(Long id) {
		// TODO
	}

	@Transactional(readOnly = true)
	public EventDT.EventResponse getEventDetail(Long eventId) {
		// TODO
		return null;
	}

	@Transactional(readOnly = true)
	public Page<EventDT.EventResponse> getAllEvents(Pageable pageable) {
		// TODO
		return null;
	}
}
