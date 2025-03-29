package KAKAO_API.projected.api.token.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token/access")
@RequiredArgsConstructor
public class AccessTokenValidateResponseController {

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(" 토큰❌ / 위조된 토큰");
        }

        String nickName = authentication.getPrincipal().toString();
        return ResponseEntity.ok("유효한 토큰✅ 사용자: " + nickName);
    }
}
