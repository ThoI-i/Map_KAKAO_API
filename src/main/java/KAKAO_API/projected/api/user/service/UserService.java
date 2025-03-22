package KAKAO_API.projected.api.user.service;

import KAKAO_API.projected.api.user.dto.RegisterRequest;
import KAKAO_API.projected.api.user.entity.UserEntity;
import KAKAO_API.projected.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 이메일 인증 통과 여부 저장용 (임시 메모리 구조)
    private final Map<String, Boolean> verifiedEmails = new HashMap<>();

    public void markEmailVerified(String email) {
        verifiedEmails.put(email, true);
    }

    public boolean isEmailVerified(String email) {
        return verifiedEmails.getOrDefault(email, false);
    }

    public void register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 1. 이메일 인증 확인
        if (!isEmailVerified(email)) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 2. 이메일 중복 체크
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 3. 닉네임은 UUID로 생성
        String nickname = UUID.randomUUID().toString();

        // 4. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 5. DB 저장
        UserEntity user = new UserEntity(nickname, email, encodedPassword);
        userRepository.save(user);
    }
}
