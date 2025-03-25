package KAKAO_API.projected.config;

import KAKAO_API.projected.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✨ 이 부분 추가!
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화 (POST 테스트용) 람다기반 DSL
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/email/**").permitAll()  // ✅이메일 인증 허용!
                        .requestMatchers("/api/user/send-code").permitAll()  // ✅회원가입 인증코드 전송 허용
                        .requestMatchers("/api/user/verify-code").permitAll()  // ✅인증코드 검증 회원가입 진행 허용
                        .requestMatchers("/api/login").permitAll()  // ✅로그인 진행 허용
                        .anyRequest().authenticated()  // 나머지는 인증 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // ✅ 명시적 필터 등록

        return http.build();
    }
}
