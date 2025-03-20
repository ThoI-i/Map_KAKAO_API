package KAKAO_API.projected.api.auth.service;

import KAKAO_API.projected.config.KakaoConfig;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoAuthService {
    private final KakaoConfig kakaoConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoAuthService(KakaoConfig kakaoConfig) {
        this.kakaoConfig = kakaoConfig;
    }

    // ✅ 카카오 토큰 요청 (인가 코드 사용)
    public Map<String, Object> getKakaoToken(String code) {
        String tokenUrl = kakaoConfig.getTokenUri();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디 설정
        Map<String, String> body = new HashMap<>();
        body.put("grant_type", "authorization_code");
        body.put("client_id", kakaoConfig.getClientId());
        body.put("redirect_uri", kakaoConfig.getRedirectUri());
        body.put("code", code);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // 카카오 API 호출 (POST 요청)
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);
        return response.getBody();
    }

    // ✅ 카카오 사용자 정보 요청 (AccessToken 사용)
    public Map<String, Object> getUserInfo(String accessToken) {
        String userInfoUrl = kakaoConfig.getUserInfoUri();

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // ✅ Authorization: Bearer {accessToken} 설정

        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 카카오 API 호출 (GET 요청)
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);
        return response.getBody();
    }
}
