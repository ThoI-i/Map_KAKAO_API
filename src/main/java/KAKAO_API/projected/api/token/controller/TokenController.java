package KAKAO_API.projected.api.token.controller;

import KAKAO_API.projected.auth.jwt.JwtTokenProvider;
import KAKAO_API.projected.auth.jwt.RedisTokenService;
import KAKAO_API.projected.api.token.dto.response.TokenResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    // ✅ 토큰 재발급 API (쿠키 기반)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {

        // ✅ 1. 쿠키에서 refresh_token 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies(); // 내 도메인의 모든 Cookie 가져옴
        if (cookies != null) { // Cookie O 실행
            for (Cookie cookie : cookies) { // 모든 Cookie 중
                if ("refresh_token".equals(cookie.getName())) { // Key 명이 refresh_token인걸 찾아
                    refreshToken = cookie.getValue(); // Value를 변수에 대입
                    break; // 반복(찾기) 종료
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ❌ 쿠키 없을 시 Error 발생
        }

        // ✅ 2. 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ❌ 만료 or 위조
        }

        // ✅ 3. 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // ✅ 4. Redis에서 저장된 RefreshToken과 비교
        String savedRefreshToken = redisTokenService.getRefreshToken(userId);
        if (!refreshToken.equals(savedRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // ❌ 위조 or 탈취
        }

        // ✅ 5. AccessToken 새로 발급
        String newAccessToken = jwtTokenProvider.createAccessToken(userId);

        // ✅ 6. 조건부 RefreshToken 재발급 (3시간 지났으면)
        long lastSavedTime = redisTokenService.getLastSavedTime(userId);
        long now = System.currentTimeMillis();
        String newRefreshToken = refreshToken;

        if (now - lastSavedTime >= 3 * 60 * 60 * 1000) {
            newRefreshToken = jwtTokenProvider.createRefreshToken();
            redisTokenService.saveRefreshToken(userId, newRefreshToken);

            // ✅ RefreshToken 재발급 시 쿠키도 갱신
            Cookie refreshCookie = new Cookie("refresh_token", newRefreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(60 * 60 * 24); // 24시간
            response.addCookie(refreshCookie);
        }

        // ✅ 7. 응답으로 AccessToken만 반환
        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }
}
