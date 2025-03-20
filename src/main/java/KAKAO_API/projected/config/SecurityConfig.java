package KAKAO_API.projected.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // ✅ CSRF 비활성화 (테스트용, 운영환경에서는 활성화 필요!)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/oauth2/**", "/login").permitAll()  // ✅ 로그인 관련 요청 허용
                        .anyRequest().authenticated()  // ✅ 그 외 요청은 인증 필요
                )
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/")  // ✅ 로그인 성공 후 이동할 페이지 설정 (예: 메인 페이지)
                        .successHandler(new OAuth2SuccessHandler())  // ✅ 로그인 성공 핸들러 (필요하면 유지)
                        .failureHandler(new OAuth2FailureHandler())  // ✅ 로그인 실패 핸들러
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")  // ✅ 로그아웃 후 리디렉트될 URL
                        .permitAll()
                );

        return http.build();
    }
}
