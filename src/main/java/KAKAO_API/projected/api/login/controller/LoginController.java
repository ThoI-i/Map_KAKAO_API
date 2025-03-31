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
    // JSON.Stringify() JS → JSON 변환 후 백엔드 전송
    // @RequestBody: JSON을 가로채 ★Jackson 라이브러리(Spring Boot) 파싱 시작(JSON → POJO) ↔ 역직렬화(Serialize)
    // = POJO(Plain Old Java Object ~ 순수한 자바 객체) → Jackson이 Reflection API(JDK)로 클래스 구조 분석
    // → 기본 생성자 호출(@NoArgsConstructor) + 각 필드 값 직접 주입(Reflection API) = POJO 완성
    // ※@JsonProperty 사용 시 Jackson이 AnnotationIntrospector 메서드로 어노테이션 분석 후
    // ★★JSON KEY = DTO 필드와 매칭시켜줌

    // EX)
    // { // JSON
    //  "email": "gmail.com", // "email" = KEY | "gmail.com" = VALUE
    //  "password": "1234"
    //}

    // RequestDTO
    // public class LoginRequest {
    //    @JsonProperty("email")  // ← "email"이라는 JSON 키 =
    //    private String emailOrNickname; // ←  = POJO 필드
    // }

        try {
            LoginResponse tokenResponse = loginService.login(request);

            // ✅ 1. RefreshToken을 쿠키에 담는다
            Cookie refreshCookie = new Cookie("refresh_token", tokenResponse.getRefreshToken());
            refreshCookie.setHttpOnly(true);          // JS 접근 차단
//            refreshCookie.setSecure(true);           // 해당 쿠키 HTTPS 환경만 전송
            refreshCookie.setSecure(false);            // 해당 쿠키 HTTP 허용 → 개발 환경
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
