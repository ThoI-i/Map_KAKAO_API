package KAKAO_API.projected.api.user.service;

import KAKAO_API.projected.api.email.service.EmailService;
import KAKAO_API.projected.api.user.dto.RegisterRequest;
import KAKAO_API.projected.api.user.entity.UserEntity;
import KAKAO_API.projected.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // 회원가입 요청 임시 저장 (이메일 → RegisterRequest)
    private final Map<String, RegisterRequest> pendingSignup = new HashMap<>();

    // 1단계 - 이메일 인증 요청 + 정보 임시 저장
    public void requestSignup(RegisterRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        emailService.sendCode(email); // ✉️ 인증코드 전송
        pendingSignup.put(email, request); // 임시 저장
    }

    // 2단계 - 인증코드 검증 후 가입 확정
    public void verifyAndRegister(String email, String code) {
        if (!emailService.verifyCode(email, code)) {
            throw new RuntimeException("인증 코드가 일치하지 않습니다.");
        }

        RegisterRequest request = pendingSignup.get(email);
        if (request == null) {
            throw new RuntimeException("가입 요청 정보가 없습니다.");
        }

        String encodedPw = passwordEncoder.encode(request.getPassword());
        UserEntity user = new UserEntity(request.getNickname(), email, encodedPw);
        userRepository.save(user);
        pendingSignup.remove(email);
    }
}
