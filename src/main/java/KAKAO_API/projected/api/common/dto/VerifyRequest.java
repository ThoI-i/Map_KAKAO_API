package KAKAO_API.projected.api.common.dto;

import lombok.Getter;

@Getter
public class VerifyRequest {
    private String email;
    private String code;
}
