package kr.co.solpick.member.controller;

import java.util.List;

import kr.co.solpick.order.dto.OrderHistoryResponseDTO;
import kr.co.solpick.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000/"})
public class MemberController {

    private final OrderService orderService;

    @GetMapping("/{memberId}/order")
    public ResponseEntity<List<OrderHistoryResponseDTO>> getOrderHistory(@PathVariable int memberId) {
        log.info("주문 내역 요청 수신: memberId={}", memberId);

        List<OrderHistoryResponseDTO> orderHistory = orderService.getOrderHistory(memberId);

        if (orderHistory.isEmpty()) {
            log.info("주문 내역 없음: memberId={}", memberId);
            return ResponseEntity.noContent().build();
        }

        log.info("주문 내역 조회 성공: memberId={}, 건수={}", memberId, orderHistory.size());
        return ResponseEntity.ok(orderHistory);
    }
}