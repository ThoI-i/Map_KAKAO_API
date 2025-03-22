package KAKAO_API.projected.api.user.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String nickname;
    private String email;
    private String password;
}

