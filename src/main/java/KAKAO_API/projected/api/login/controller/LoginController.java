package KAKAO_API.projected.api.login.controller;

import KAKAO_API.projected.api.login.dto.request.LoginRequest;
import KAKAO_API.projected.api.login.dto.response.LoginResponse;
import KAKAO_API.projected.api.login.service.LoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            LoginResponse tokenResponse = loginService.login(request);

            // ✅ 1. RefreshToken을 쿠키에 담는다
            Cookie refreshCookie = new Cookie("refresh_token", tokenResponse.getRefreshToken());
            refreshCookie.setHttpOnly(true);          // JS 접근 차단
            refreshCookie.setSecure(true);            // HTTPS 환경에서만 전송
            refreshCookie.setPath("/");               // 모든 요청에 대해 전송됨
            refreshCookie.setMaxAge(60 * 60 * 24);    // 24시간 유효

            // ✅ 2. 응답에 쿠키 추가
            response.addCookie(refreshCookie);

            // ✅ 3. AccessToken은 그대로 body로 전달
            return ResponseEntity.ok(tokenResponse);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
