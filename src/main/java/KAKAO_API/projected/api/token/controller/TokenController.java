package KAKAO_API.projected.api.token.controller;

import KAKAO_API.projected.auth.jwt.JwtTokenProvider;
import KAKAO_API.projected.auth.jwt.RedisTokenService;
import KAKAO_API.projected.api.token.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;

    // 🎯 토큰 재발급 API
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestHeader("Authorization") String refreshTokenHeader) {

        // 1. 헤더에서 "Bearer " 접두사 제거
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");

        // 2. 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ❌ 유효하지 않음
        }

        // 3. 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // 4. Redis에서 저장된 RefreshToken 불러오기
        String savedRefreshToken = redisTokenService.getRefreshToken(userId);

        // 5. 저장된 토큰과 비교
        if (!refreshToken.equals(savedRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ❌ 위조 또는 만료
        }

        // 6. Access Token 새로 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        // 7. 3시간 이상 경과 시 RefreshToken도 재발급
        long lastSavedTime = redisTokenService.getLastSavedTime(userId);
        long now = System.currentTimeMillis();
        String newRefreshToken = refreshToken; // 기본은 그대로 사용

        if (now - lastSavedTime >= 3 * 60 * 60 * 1000) {
            newRefreshToken = jwtTokenProvider.createRefreshToken();
            redisTokenService.saveRefreshToken(userId, newRefreshToken); // Redis도 갱신!
        }
        // 8. 응답
        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }
}
