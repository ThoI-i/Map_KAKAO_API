package KAKAO_API.projected.api.login.controller;

import KAKAO_API.projected.api.login.dto.request.LoginRequest;
import KAKAO_API.projected.api.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            loginService.login(request);
            return ResponseEntity.ok("🎉 로그인 성공!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
