package kr.co.solpick.point.service;

import kr.co.solpick.card.repository.CardRepository;
import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.point.entity.Point;
import kr.co.solpick.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kr.co.solpick.card.entity.Card;

import java.util.Optional;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;
    private final CardRepository cardRepository;


    /**
     * 솔픽 회원 ID로 포인트 조회 <- 이건 추 후 수정
     */
    @Transactional(readOnly = true)
    public int getUserPoints(int userId) {
        log.info("솔픽 회원 ID로 포인트 조회: userId={}", userId);
        return pointRepository.findLatestPointBalanceByUserId(userId)
                .orElse(0);
    }

    /**
     * 레시픽 회원 ID로 포인트 조회
     */
    @Transactional(readOnly = true)
    public int getUserPointsByRecipickUserId(int recipickUserId) {
        log.info("레시픽 회원 ID로 포인트 조회: recipickUserId={}", recipickUserId);

        return pointRepository.findLatestPointBalanceByRecipickUserId(recipickUserId)
                .orElseGet(() -> {
                    log.warn("레시픽 회원 ID에 해당하는 포인트 정보가 없습니다: recipickUserId={}", recipickUserId);
                    return 0;
                });
    }

    /**
     * 포인트 사용 처리
     * @param recipickUserId 레시픽 사용자 ID
     * @param orderId 주문 ID
     * @param pointsToUse 사용할 포인트 양
     * @param totalPrice 주문 총액
     * @return 성공 여부
     */
    @Transactional
    public boolean usePoints(int recipickUserId, int orderId, int pointsToUse, int totalPrice) {
        log.info("포인트 사용 처리: recipickUserId={}, orderId={}, pointsToUse={}, totalPrice={}",
                recipickUserId, orderId, pointsToUse, totalPrice);

        // 레시픽 회원 ID로 솔픽 회원 조회
        Optional<Member> memberOpt = memberRepository.findByRecipickUserId(recipickUserId);
        if (memberOpt.isEmpty()) {
            log.warn("회원 정보를 찾을 수 없음: recipickUserId={}", recipickUserId);
            return false;
        }

        Member member = memberOpt.get();
        int userId = member.getId();

        // 사용자의 카드 조회 (사용자당 카드가 하나인 경우)
        Optional<Card> cardOpt = cardRepository.findByUserId(userId);
        if (cardOpt.isEmpty()) {
            log.warn("사용자의 카드 정보를 찾을 수 없음: userId={}", userId);
            return false;
        }

        Card card = cardOpt.get();
        Integer cardId = card.getId();

        // 현재 포인트 잔액 조회
        int currentPointBalance = pointRepository.findLatestPointBalanceByUserId(userId)
                .orElse(0);

        // 포인트 부족 체크
        if (currentPointBalance < pointsToUse) {
            log.warn("포인트 부족: currentBalance={}, requested={}", currentPointBalance, pointsToUse);
            return false;
        }

        // 포인트 사용 내역 저장
        Point pointUsage = Point.builder()
                .userId(userId)
                .cardId(cardId)  // 카드 ID 설정
                .orderId(orderId)
                .pointAmount(-pointsToUse)
                .pointBalance(currentPointBalance - pointsToUse)
                .pointType("USE")
                .transactionAmount(totalPrice)
                .description("레시픽 주문")
//                .createdDate(LocalDate.now())
                .build();

        pointRepository.save(pointUsage);
        log.info("포인트 사용 처리 완료: userId={}, pointsUsed={}, newBalance={}",
                userId, pointsToUse, pointUsage.getPointBalance());

        // 구매 금액의 2% 적립 (솔픽 카드 혜택)
        int earnedPoints = (int)(totalPrice * 0.02);
        if (earnedPoints > 0) {
            Point pointEarning = Point.builder()
                    .userId(userId)
                    .cardId(cardId)  // 카드 ID 설정
                    .orderId(orderId)
                    .pointAmount(earnedPoints)
                    .pointBalance(pointUsage.getPointBalance() + earnedPoints)
                    .pointType("EARN")
                    .transactionAmount(totalPrice)
                    .description("레시픽 주문")
//                    .createdDate(LocalDate.now())
                    .build();

            pointRepository.save(pointEarning);
            log.info("포인트 적립 처리 완료: userId={}, earnedPoints={}, newBalance={}",
                    userId, earnedPoints, pointEarning.getPointBalance());
        }

        return true;
    }
}
