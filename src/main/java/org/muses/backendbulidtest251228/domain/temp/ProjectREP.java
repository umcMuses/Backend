package org.muses.backendbulidtest251228.domain.temp;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProjectREP extends JpaRepository<Project, Long> {

    //마감대상 찾기
    @Query("""
      SELECT p FROM Project p
      WHERE p.fundingStatus='FUNDING' AND p.deadline <= :now
      ORDER BY p.deadline ASC, p.id ASC
    """)
    List<Project> findExpiredActiveProjects(@Param("now") LocalDateTime now, Pageable pageable);


    //선점 : FUNDING -> CLOSING
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      UPDATE Project p
      SET p.fundingStatus='CLOSING', p.updatedAt=:now
      WHERE p.id=:id AND p.status='FUNDING'
    """)
    int tryAcquireClosing(@Param("id") Long id, @Param("now") LocalDateTime now);

    // CLOSING 에서 SUCCESS/FAILED 로 확정
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      UPDATE Project p
      SET p.fundingStatus=:status, p.updatedAt=:now
      WHERE p.id=:id AND p.fundingStatus='CLOSING'
    """)
    int finalizeFromClosing(@Param("id") Long id,
                            @Param("status") FundingStatus status,
                            @Param("now") LocalDateTime now);


    //청소배치로 오래 멈춘 프로젝트를 찾아낸다
    @Query("""
      SELECT p FROM Project p
      WHERE p.status='CLOSING' AND p.updatedAt < :threshold
      ORDER BY p.updatedAt ASC
    """)
    List<Project> findStuckClosing(@Param("threshold") LocalDateTime threshold, Pageable pageable);


}
