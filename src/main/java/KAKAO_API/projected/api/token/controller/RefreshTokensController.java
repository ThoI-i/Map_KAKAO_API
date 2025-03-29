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
public class RefreshTokensController {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;

    // ✅ Refresh Token 재발급 API (쿠키 기반)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        // HttpServlet : 통신 규칙 처리자(클라이언트 ↔ 서버의 연결다리 역할)
        // request  : [클라이언트 → 서버] 쿠키, 파라미터, 헤더 "정보 요청"
        // response : [서버 → 클라이언트] 쿠키, 파라미터, 헤더 "정보 응답"

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
        if (!jwtTokenProvider.validateToken(refreshToken)) { // ❌ 위조:.setSigningKey | 만료: .parseClaimsJws

            Cookie expired = new Cookie("refresh_token", null);
            clearRefreshTokenCookie(response); // ❌ 위조/만료 쿠키 삭제 메서드 호출
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // ✅ 3. 사용자 ID 추출
        String userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // ✅ 4. Redis에 저장된 RefreshToken과 비교 → Redis에 없는 RefreshToken 삭제
        String savedRefreshToken = redisTokenService.getRefreshToken(userId);
        if (!refreshToken.equals(savedRefreshToken)) { // ❌ 위조/탈취
            clearRefreshTokenCookie(response); // ❌ 위조/만료 쿠키 삭제 메서드 호출

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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

    // ❌ 위조/만료 쿠키 삭제 메서드
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie expired = new Cookie("refresh_token", null);
        expired.setMaxAge(0);          // ⏱ Max-Age(TTL) 0초 → 즉시 삭제
        expired.setHttpOnly(true);     // JS 접근금지(보안)
        expired.setSecure(true);       // HTTPS에서만 전송
        expired.setPath("/");          // 모든 경로에서 일치 시 삭제
        response.addCookie(expired);   // 브라우저에게 쿠키 삭제 명령 전달
    }
}
