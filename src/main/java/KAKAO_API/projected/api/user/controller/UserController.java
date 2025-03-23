package KAKAO_API.projected.api.user.controller;

import KAKAO_API.projected.api.user.service.UserService;
import KAKAO_API.projected.api.common.dto.VerifyRequest;
import KAKAO_API.projected.api.user.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody RegisterRequest request) {
        try {
            userService.requestSignup(request);
            return ResponseEntity.ok("📩 인증번호가 전송되었습니다!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyRequest request) {
        try {
            userService.verifyAndRegister(request.getEmail(), request.getCode());
            return ResponseEntity.ok("🎉 회원가입이 완료되었습니다!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("❌ " + e.getMessage());
        }
    }
}
