package KAKAO_API.projected.api.login.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String emailOrNickname;
    private String password;
}
