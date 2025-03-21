package KAKAO_API.projected.api.auth.controller;

import KAKAO_API.projected.api.auth.service.KakaoAuthService;
import KAKAO_API.projected.api.entity.UserEntity;
import KAKAO_API.projected.api.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final KakaoAuthService kakaoAuthService;
    private final UserRepository userRepository;

    public AuthController(KakaoAuthService kakaoAuthService, UserRepository userRepository) {
        this.kakaoAuthService = kakaoAuthService;
        this.userRepository = userRepository;
    }

    // ✅ 1. 로그인 상태 체크 API (프론트에서 호출)
    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Unauthorized");  // ❌ 토큰 없음 → 로그인 필요
        }

        String accessToken = token.substring(7); // "Bearer " 제거 후 실제 토큰 추출

        // ✅ 토큰 검증 (카카오 API 호출해서 사용자 정보 확인)
        Map<String, Object> userInfo = kakaoAuthService.getUserInfo(accessToken);

        if (userInfo == null || userInfo.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid token"); // ❌ 토큰이 유효하지 않음
        }

        return ResponseEntity.ok(userInfo); // ✅ 유효한 토큰 → 로그인 유지
    }

    // ✅ 2. 카카오 로그인 요청 (인가 코드 받아서 토큰 요청)
    @GetMapping("/kakao-login")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code) {
        Map<String, Object> tokenResponse = kakaoAuthService.getKakaoToken(code);
        return ResponseEntity.ok(tokenResponse);  // ✅ 토큰 반환
    }

    // ✅ 3. 카카오 콜백 (토큰 요청 및 사용자 정보 조회 후 저장)
    @GetMapping("/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        Map<String, Object> tokenResponse = kakaoAuthService.getKakaoToken(code); // ✅ 토큰 요청
        String accessToken = (String) tokenResponse.get("access_token");

        Map<String, Object> userInfo = kakaoAuthService.getUserInfo(accessToken); // ✅ 사용자 정보 요청

        // ✅ DB에서 사용자 확인 및 저장
        String kakaoId = userInfo.get("kakaoId").toString();
        String nickname = userInfo.get("nickname").toString();
        String profileImage = userInfo.get("profileImage").toString();

        Optional<UserEntity> existingUser = userRepository.findByKakaoId(kakaoId);
        if (existingUser.isEmpty()) {
            userRepository.save(new UserEntity(kakaoId, nickname, profileImage));
        }

        return ResponseEntity.ok(tokenResponse);  // ✅ 토큰 응답
    }
}
