package KAKAO_API.projected.api.login.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // Jackson 라이브러리: 파싱[JSON → POJO]: 기본 생성자(@NoArgsConstructor) + Reflection API) → POJO 완성
public class LoginRequest {
    private String emailOrNickname;
    private String password;
}
