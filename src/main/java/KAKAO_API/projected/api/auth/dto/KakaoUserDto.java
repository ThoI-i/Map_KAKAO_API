package KAKAO_API.projected.api.auth.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class KakaoUserDto {
    private String id;
    private String nickname;
    private String profileImage;
    private String email;

    // ✅ 카카오 API 응답에서 필요한 정보만 가져와서 DTO로 변환
    public KakaoUserDto(Map<String, Object> attributes) {
        this.id = String.valueOf(attributes.get("id"));  // ✅ 사용자 고유 ID

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties != null) {
            this.nickname = (String) properties.get("nickname");  // ✅ 닉네임
            this.profileImage = (String) properties.get("profile_image");  // ✅ 프로필 이미지 URL
        }

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        if (kakaoAccount != null) {
            this.email = (String) kakaoAccount.get("email");  // ✅ 이메일 (필수 동의 필요)
        }
    }
}
