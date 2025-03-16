package kr.co.solpick.api.external.payment.controller;

import kr.co.solpick.api.external.payment.dto.VerifyCardRequestDTO;
import kr.co.solpick.api.external.payment.dto.VerifyCardResponseDTO;
import kr.co.solpick.api.external.payment.service.CardValidationService;
import kr.co.solpick.api.external.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/solpick/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final CardValidationService cardValidationService;
    private final ApiKeyService apiKeyService;

    @PostMapping("/verify-card")
    public VerifyCardResponseDTO verifyCard(@RequestBody VerifyCardRequestDTO request) {
        log.info("카드 검증 API 요청 받음: recipickUserId={}", request.getRecipickUserId());

        // API 키 유효성 검사
        if (!apiKeyService.validateApiKey(request.getApiKey())) {
            log.warn("잘못된 API 키: {}", request.getApiKey());
            return VerifyCardResponseDTO.builder()
                    .success(false)
                    .message("유효하지 않은 API 키입니다.")
                    .isValid(false)
                    .build();
        }

        try {
            // 카드 검증 서비스 호출
            return cardValidationService.verifyCardAndUsePoints(
                    request.getRecipickUserId(),
                    request.getCardNumber(),
                    request.getCardExpiry()
            );
        } catch (Exception e) {
            log.error("카드 검증 중 오류 발생", e);
            return VerifyCardResponseDTO.builder()
                    .success(false)
                    .message("카드 검증 중 오류가 발생했습니다: " + e.getMessage())
                    .isValid(false)
                    .build();
        }
    }
}