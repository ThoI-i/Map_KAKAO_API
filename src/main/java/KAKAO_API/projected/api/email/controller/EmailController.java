package KAKAO_API.projected.api.email.controller;

import KAKAO_API.projected.api.email.dto.VerifyRequest;
import KAKAO_API.projected.api.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    /**
     * 이메일 인증 코드 요청 API
     * @param email 인증을 요청할 사용자 이메일
     * @return 인증 코드 전송 결과
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        String code = emailService.sendCode(email); // 콘솔 출력용 코드
        return ResponseEntity.ok("인증 코드가 이메일로 전송됨");
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody VerifyRequest request) {
        boolean result = emailService.verifyCode(request.getEmail(), request.getCode());

        if (result) {
            return ResponseEntity.ok("✅ 이메일 인증 성공!");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ 인증 실패! 코드가 틀렸거나 만료되었습니다.");
        }
    }

}
