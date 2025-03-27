package KAKAO_API.projected.api.login.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

//@Data // getter, setter, Tosting, NoArgs, AllArgs 포함 @Data 금지
@Getter
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String accessToken;
    private String refreshToken;
}
