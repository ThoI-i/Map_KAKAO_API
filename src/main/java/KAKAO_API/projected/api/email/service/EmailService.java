package KAKAO_API.projected.api.email.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final Map<String, AuthCode> codeStorage = new HashMap<>();

    public String sendCode(String email) {
        String code = generateCode();
        codeStorage.put(email, new AuthCode(code, LocalDateTime.now().plusMinutes(5)));
        sendMail(email, code);  // ✉️ 전송!
        return code;
    }

    private void sendMail(String to, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("[MemoryEat] 회원가입 인증 코드 안내");
        message.setText("✅ 인증 코드: " + code + "\n\n" +
                "⚠ 위 인증 코드는 5분 후 만료됩니다.");

        mailSender.send(message);
    }

    // 🔽 잘못 클래스 바깥에 나가 있었던 것들 🔽

    public boolean verifyCode(String email, String inputCode) {
        AuthCode saved = codeStorage.get(email);
        if (saved == null) return false;
        if (saved.getExpireTime().isBefore(LocalDateTime.now())) return false;

        return saved.getCode().equals(inputCode);
    }

    private String generateCode() {
        Random r = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(r.nextInt(10));  // 0 ~ 9 숫자 중 랜덤
        }
        return sb.toString();
    }

    @Getter
    static class AuthCode {
        private final String code;
        private final LocalDateTime expireTime;

        public AuthCode(String code, LocalDateTime expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }
}
