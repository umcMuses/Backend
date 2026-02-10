package org.muses.backendbulidtest251228.domain.event.service;

import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.event.dto.EventDetailResDTO;
import org.muses.backendbulidtest251228.domain.event.dto.EventResDTO;
import org.muses.backendbulidtest251228.domain.event.entity.Event;
import org.muses.backendbulidtest251228.domain.event.repository.EventRepo;
import org.muses.backendbulidtest251228.global.apiPayload.ApiResponse;
import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSRV {

    private final EventRepo eventRepo;


    public ApiResponse<List<EventResDTO>> getAllEvents(
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable =
                PageRequest.of(page, size, Sort.by("uploadDateTime").descending());

        Page<EventResDTO> resultPage =
                eventRepo.findEventPage(keyword, pageable);

        ApiResponse.PageInfo pageInfo =
                new ApiResponse.PageInfo(
                        resultPage.getNumber(),        // offset
                        resultPage.getSize(),          // limit
                        resultPage.getTotalElements()  // total
                );

        return ApiResponse.success(resultPage.getContent(), pageInfo);
    }


    public ApiResponse<EventDetailResDTO> getEventDetail(Long eventId) {

        Event event = eventRepo.findPublishedById(eventId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "해당 이벤트를 찾을 수 없습니다."));

        EventResDTO eventDto = new EventResDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getContent(),
                event.getCategory(),
                event.getUploadDateTime()
        );

        Long prevId = eventRepo
                .findPrevIds(event.getUploadDateTime(), event.getId(), PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);

        Long nextId = eventRepo
                .findNextIds(event.getUploadDateTime(), event.getId(), PageRequest.of(0, 1))
                .stream().findFirst().orElse(null);

        EventDetailResDTO res = new EventDetailResDTO(eventDto, prevId, nextId);

        return ApiResponse.success(res);
    }
}
