package org.muses.backendbulidtest251228.domain.checkin.generator;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import org.muses.backendbulidtest251228.domain.member.entity.Member;
import org.muses.backendbulidtest251228.domain.orderItem.entity.OrderItemENT;
import org.muses.backendbulidtest251228.domain.project.entity.RewardENT;
import org.muses.backendbulidtest251228.domain.project.repository.RewardRepo;
import org.muses.backendbulidtest251228.domain.ticket.entity.TicketENT;
import org.muses.backendbulidtest251228.domain.ticket.repository.TicketRepo;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class QrGenerator {

    private final TicketRepo ticketRepo;

    private final RewardRepo rewardRepo;

    // 문자열(URL)을 QR 코드 PNG(byte[])로 변환한다
    public byte[] generatePng(String text, String ticketUrl, Long ticketId, Member member) {
        try {

            TicketENT ticketENT = ticketRepo.findByIdWithOrderItem(ticketId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓"));

            OrderItemENT orderItem = ticketENT.getOrderItem();

            RewardENT rewardENT = rewardRepo.findById(orderItem.getRewardId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리워드"));

            //  QR에 넣을 데이터
            String name = URLEncoder.encode(member.getName(), StandardCharsets.UTF_8);
            String nick = URLEncoder.encode(member.getNickName(), StandardCharsets.UTF_8);
            String reward = URLEncoder.encode(rewardENT.getRewardName(), StandardCharsets.UTF_8);
            Integer qty = orderItem.getQuantity();

            // ticketUrl 예:
            // https://muses.site/checkin/{checkinToken}?t={ticketToken}
            String qrText = ticketUrl
                    + (ticketUrl.contains("?") ? "&" : "?")
                    + "&ticketId=" + ticketId
                    + "&name=" + URLEncoder.encode(member.getName(), StandardCharsets.UTF_8)
                    + "&nick=" + URLEncoder.encode(member.getNickName(), StandardCharsets.UTF_8)
                    + "&qty=" + orderItem.getQuantity()
                    + "&reward=" + URLEncoder.encode(rewardENT.getRewardName(), StandardCharsets.UTF_8);


            BitMatrix matrix = new MultiFormatWriter()
                    .encode(qrText, BarcodeFormat.QR_CODE, 320, 320);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("QR 생성 실패", e);
        }
    }
}

