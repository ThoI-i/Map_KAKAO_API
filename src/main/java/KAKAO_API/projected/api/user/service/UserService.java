package KAKAO_API.projected.api.user.service;

import KAKAO_API.projected.api.user.dto.RegisterRequest;
import KAKAO_API.projected.api.user.entity.UserEntity;
import KAKAO_API.projected.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String uuidNickname = UUID.randomUUID().toString();

        UserEntity user = new UserEntity(uuidNickname, request.getEmail(), encodedPassword);
        userRepository.save(user);
    }
}
