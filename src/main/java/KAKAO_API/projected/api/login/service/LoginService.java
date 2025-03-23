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
        String input = request.getEmailOrNickname();
        UserEntity user = userRepository.findByEmail(input)
                .or(() -> userRepository.findByNickname(input))
                .orElseThrow(() -> new RuntimeException("❌ 이메일 또는 닉네임이 잘못되었습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("❌ 비밀번호가 일치하지 않습니다.");
        }

        // 로그인 성공 처리 (토큰 발급 예정)
    }

}
