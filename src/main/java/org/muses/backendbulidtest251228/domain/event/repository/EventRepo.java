package org.muses.backendbulidtest251228.domain.event.repository;

import org.muses.backendbulidtest251228.domain.event.dto.EventResDTO;
import org.muses.backendbulidtest251228.domain.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepo extends JpaRepository<Event, Long> {
	// 관리자용 전체 조회
	Page<Event> findAll(Pageable pageable);


    // 이벤트 카드 조회
    @Query("""
    select new org.muses.backendbulidtest251228.domain.event.dto.EventResDTO(
        e.id,
        e.title,
        e.description,
        e.content,
        e.category,
        e.uploadDateTime
    )
    from Event e
    where e.uploadDateTime <= current_timestamp
      and (:keyword is null or :keyword = ''
           or e.title like concat('%', :keyword, '%'))
    order by e.uploadDateTime desc
""")
    Page<EventResDTO> findEventPage(
            @Param("keyword") String keyword,
            Pageable pageable
    );


    @Query("""
        select e
        from Event e
        where e.id = :eventId
          and e.uploadDateTime <= current_timestamp
    """)
    Optional<Event> findPublishedById(@Param("eventId") Long eventId);



    @Query("""
    select e.id
    from Event e
    where e.uploadDateTime <= current_timestamp
      and (
            e.uploadDateTime > :currentUpload
            or (e.uploadDateTime = :currentUpload and e.id > :currentId)
      )
    order by e.uploadDateTime asc, e.id asc
""")
    List<Long> findPrevIds(
            @Param("currentUpload") LocalDateTime currentUpload,
            @Param("currentId") Long currentId,
            Pageable pageable
    );

    @Query("""
    select e.id
    from Event e
    where e.uploadDateTime <= current_timestamp
      and (
            e.uploadDateTime < :currentUpload
            or (e.uploadDateTime = :currentUpload and e.id < :currentId)
      )
    order by e.uploadDateTime desc, e.id desc
""")
    List<Long> findNextIds(
            @Param("currentUpload") LocalDateTime currentUpload,
            @Param("currentId") Long currentId,
            Pageable pageable
    );

}
