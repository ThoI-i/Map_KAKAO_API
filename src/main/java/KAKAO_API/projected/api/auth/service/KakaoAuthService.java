package KAKAO_API.projected.api.auth.service;

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
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();  // ✅ JSON 파싱을 위한 ObjectMapper

    public KakaoAuthService(KakaoConfig kakaoConfig) {
        this.kakaoConfig = kakaoConfig;
    }

    // ✅ 카카오 토큰 요청 (인가 코드 사용)
    public Map<String, Object> getKakaoToken(String code) {
        String tokenUrl = kakaoConfig.getTokenUri();

        // ✅ 요청 바디 설정 (MultiValueMap 사용)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoConfig.getClientId());
        body.add("redirect_uri", kakaoConfig.getRedirectUri());
        body.add("code", code);

        // ✅ 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // ✅ 카카오 API 호출 (POST 요청)
            ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, String.class);
            return objectMapper.readValue(response.getBody(), Map.class); // ✅ JSON 파싱
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 요청 실패: " + e.getMessage());
        }
    }

    // ✅ 카카오 사용자 정보 요청 (AccessToken 사용)
    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = kakaoConfig.getUserInfoUri();

        // ✅ 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // ✅ Authorization: Bearer {accessToken}

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            // ✅ 카카오 API 호출 (GET 요청)
            ResponseEntity<String> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, String.class);
            JsonNode userInfoJson = objectMapper.readTree(response.getBody()); // ✅ JSON 파싱

            // ✅ 닉네임과 프로필 사진만 추출
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userInfoJson.get("id").asText());
            userInfo.put("nickname", userInfoJson.path("properties").path("nickname").asText());
            userInfo.put("profile_image", userInfoJson.path("properties").path("profile_image").asText());

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 요청 실패: " + e.getMessage());
        }
    }
}
