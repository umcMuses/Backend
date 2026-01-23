package org.muses.backendbulidtest251228.domain.order.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateResDT {

    private Long orderId;

    private String customerKey;

    private String successUrl;

    private String failUrl;
}
