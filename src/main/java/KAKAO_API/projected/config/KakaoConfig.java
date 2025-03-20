package KAKAO_API.projected.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao")  // ✅ application.yml에서 "kakao" 설정을 가져옴
public class KakaoConfig {
    private String clientId;
    private String redirectUri;
    private String tokenUri;
    private String userInfoUri;
}
