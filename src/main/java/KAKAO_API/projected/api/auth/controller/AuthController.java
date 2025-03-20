package KAKAO_API.projected.api.auth.controller;

import KAKAO_API.projected.api.auth.service.KakaoAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final KakaoAuthService kakaoAuthService;

    public AuthController(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    // ✅ 카카오 로그인 요청 (인가 코드 받아서 토큰 요청)
    @GetMapping("/kakao-login")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) {
        Map<String, Object> tokenResponse = kakaoAuthService.getKakaoToken(code);  // ✅ 정상적으로 Map으로 반환됨
        return ResponseEntity.ok(tokenResponse);
    }

    // ✅ 카카오 사용자 정보 요청 (토큰 필요)
    @GetMapping("/kakao/user")
    public ResponseEntity<?> getUserInfo(@RequestParam String accessToken) {
        Map<String, Object> userInfo = kakaoAuthService.getUserInfo(accessToken);
        return ResponseEntity.ok(userInfo);
    }
}
