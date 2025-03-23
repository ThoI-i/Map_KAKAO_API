package KAKAO_API.projected.api.login.service;

import KAKAO_API.projected.api.login.dto.request.LoginRequest;
import KAKAO_API.projected.api.user.entity.UserEntity;
import KAKAO_API.projected.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("❌ 해당 이메일은 존재하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("❌ 비밀번호가 일치하지 않습니다.");
        }

        // 👇 추후 여기에 토큰 발급 추가할 예정!
    }
}
