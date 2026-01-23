package org.muses.backendbulidtest251228.domain.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.muses.backendbulidtest251228.domain.project.dto.response.ProjectCardResponseDT;

import java.util.List;

@Mapper
public interface ProjectMapper {

    // 프로젝트 카드 목록 조회 (필터링 + 페이징)
    List<ProjectCardResponseDT> findProjectCards(
            @Param("region") String region,
            @Param("fundingStatus") String fundingStatus,
            @Param("tag") String tag,
            @Param("keyword") String keyword,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    // 전체 개수 조회 (페이징용)
    int countProjectCards(
            @Param("region") String region,
            @Param("fundingStatus") String fundingStatus,
            @Param("tag") String tag,
            @Param("keyword") String keyword
    );
}
