package KAKAO_API.projected.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✨ 이 부분 추가!
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // CSRF 보호 비활성화 (POST 테스트용)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/email/**").permitAll()  // 이메일 인증 관련은 허용!
                        .anyRequest().authenticated()  // 나머지는 인증 필요
                );

        return http.build();
    }
}
