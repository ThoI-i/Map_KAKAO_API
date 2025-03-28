package KAKAO_API.projected.api.login.service;

import KAKAO_API.projected.api.login.dto.request.LoginRequest;
import KAKAO_API.projected.api.login.dto.response.LoginResponse;
import KAKAO_API.projected.api.user.entity.UserEntity;
import KAKAO_API.projected.api.user.repository.UserRepository;
import KAKAO_API.projected.auth.jwt.JwtTokenProvider;
import KAKAO_API.projected.auth.jwt.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenService redisTokenService;

    public LoginResponse login(LoginRequest request) {
        String input = request.getEmailOrNickname();
        UserEntity user = userRepository.findByEmail(input)
                .or(() -> userRepository.findByNickname(input))
                .orElseThrow(() -> new RuntimeException("❌ 이메일 또는 닉네임이 잘못되었습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("❌ 비밀번호가 일치하지 않습니다.");
        }

// ✅ 토큰 생성
String accessToken = jwtTokenProvider.createAccessToken(user.getNickname().toString());
//String accessToken = jwtTokenProvider.createAccessToken(user.getNickname().toString()
//                                                        ,user.getEmail().toString()
//                                                        ,user.getRole().toString());
// VO(Value Object) 사용 시, .toString() 반환 타입을 명시 필요
        String refreshToken = jwtTokenProvider.createRefreshToken();

        // ✅ Redis 저장
        redisTokenService.saveRefreshToken(user.getNickname().toString(), refreshToken);

        return new LoginResponse("🎉 로그인 성공!", accessToken, refreshToken);
    }
}