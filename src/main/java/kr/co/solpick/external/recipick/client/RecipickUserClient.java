//package kr.co.solpick.external.recipick.client;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//import org.zerock.solpick.external.recipick.dto.RecipickMemberRequestDTO;
//import org.zerock.solpick.external.recipick.dto.RecipickMemberResponseDTO;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class RecipickUserClient {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${recipick.api.key}")
//    private String apiKey;
//
//    @Value("${recipick.api.base-url}")
//    private String baseUrl;
//
//    /**
//     * Recipick API에서 회원 정보 조회
//     */
//    public RecipickMemberResponseDTO getUserInfo(String email) {
//        String url = baseUrl + "/api/members/info";
//
//        RecipickMemberRequestDTO request = new RecipickMemberRequestDTO();
//        request.setEmail(email);
//        request.setApiKey(apiKey);
//
//        try {
//            log.info("Recipick 회원 정보 API 호출: {}", email);
//            ResponseEntity<RecipickMemberResponseDTO> response =
//                    restTemplate.postForEntity(url, request, RecipickMemberResponseDTO.class);
//
//            if (response.getBody() == null) {
//                log.error("Recipick 회원 정보 조회 실패: 응답이 비어있습니다");
//                throw new RuntimeException("회원 정보를 조회할 수 없습니다");
//            }
//
//            log.info("Recipick 회원 정보 조회 성공: {}", email);
//            return response.getBody();
//        } catch (Exception e) {
//            log.error("Recipick 회원 정보 API 호출 중 오류 발생: {}", e.getMessage());
//            throw new RuntimeException("회원 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
//        }
//    }
//}