package KAKAO_API.projected.api.email.dto;

import lombok.Getter;

@Getter
public class VerifyRequest {
    private String email;
    private String code;
}
