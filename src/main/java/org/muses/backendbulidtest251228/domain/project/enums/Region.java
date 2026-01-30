package org.muses.backendbulidtest251228.domain.project.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 지역 Enum
 * 
 * NOTE: 현재 projects 테이블에서만 사용
 * TODO: 추후 지역 추가/수정이 빈번하면 별도 테이블로 분리 논의 필요
 */
@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("서울"),
    GYEONGGI("경기"),
    INCHEON("인천"),
    BUSAN("부산"),
    DAEGU("대구"),
    DAEJEON("대전"),
    GWANGJU("광주"),
    ULSAN("울산"),
    SEJONG("세종"),
    GANGWON("강원"),
    CHUNGBUK("충북"),
    CHUNGNAM("충남"),
    JEONBUK("전북"),
    JEONNAM("전남"),
    GYEONGBUK("경북"),
    GYEONGNAM("경남"),
    JEJU("제주");

    private final String displayName;
}
