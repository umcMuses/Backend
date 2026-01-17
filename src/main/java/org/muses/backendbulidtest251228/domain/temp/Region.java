package org.muses.backendbulidtest251228.domain.temp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Region {
    SEOUL("서울"),
    GYEONGGI("경기"),
    INCHEON("인천"),
    BUSAN("부산"),
    GWANGJU("광주"),
    DAEGU("대구"),
    DAEJEON("대전"),
    ULSAN("울산"),
    JEJU("제주"),
    GANGWON("강원"),
    GYEONGNAM("경남"),
    GYEONGBUK("경북"),
    JEONNAM("전남"),
    JEONBUK("전북");

    private final String description;
}
