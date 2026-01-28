package org.muses.backendbulidtest251228.domain.event.repository;

import org.muses.backendbulidtest251228.domain.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepo extends JpaRepository<Event, Long> {
	// 관리자용 전체 조회
	Page<Event> findAll(Pageable pageable);
}
