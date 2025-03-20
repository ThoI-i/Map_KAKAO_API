package KAKAO_API.projected.api.auth.service;

import KAKAO_API.projected.api.entity.UserEntity;
import KAKAO_API.projected.api.repository.UserRepository;
import KAKAO_API.projected.config.KakaoConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoAuthService {
    private final KakaoConfig kakaoConfig;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoAuthService(KakaoConfig kakaoConfig, UserRepository userRepository) {
        this.kakaoConfig = kakaoConfig;
        this.userRepository = userRepository;
    }

    // ✅ 1. 카카오 토큰 요청 (인가 코드 사용)
    public Map<String, Object> getKakaoToken(String code) {
        String tokenUrl = kakaoConfig.getTokenUri();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoConfig.getClientId());
        body.add("redirect_uri", kakaoConfig.getRedirectUri());
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);

        try {
            // ✅ JSON String → Map<String, Object> 변환
            return objectMapper.readValue(response.getBody(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 요청 실패: " + e.getMessage());
        }
    }

    // ✅ 2. 카카오 사용자 정보 요청 + DB 저장
    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = kakaoConfig.getUserInfoUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);

        try {
            JsonNode userInfoJson = objectMapper.readTree(response.getBody());

            // ✅ 유저 정보 추출
            String kakaoId = userInfoJson.get("id").asText();
            String nickname = userInfoJson.path("properties").path("nickname").asText();
            String profileImage = userInfoJson.path("properties").path("profile_image").asText();

            // ✅ 유저 정보를 DB에 저장 (중복 체크)
            userRepository.findByKakaoId(kakaoId)
                    .orElseGet(() -> userRepository.save(new UserEntity(kakaoId, nickname, profileImage)));

            // ✅ 반환할 유저 정보
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("kakaoId", kakaoId);
            userInfo.put("nickname", nickname);
            userInfo.put("profileImage", profileImage);

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + e.getMessage());
        }
    }
}
