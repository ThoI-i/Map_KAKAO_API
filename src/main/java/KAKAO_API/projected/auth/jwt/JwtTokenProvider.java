package KAKAO_API.projected.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    // ✅ 기존 secretKey를 문자열로 두지 않고 Key 객체로 처리
    private final String secretKey = "JWT_MASTER_SECRET_123456789012345678901234"; // 최소 32바이트 이상 필수!

    // 🔒 JJWT 0.11.x 이상에서는 문자열 secretKey 사용이 deprecated됨
    //     → 반드시 Key 객체를 사용해야 함
    private final Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    // 32byte 이하의 secretKey 사용 시 Error 발생: io.jsonwebtoken.security.WeakKeyException
    // .getBytes 메서드로 객체가 32byte 이상인지 검증

    // ⏱️ 만료 시간은 기존 그대로 유지
    private final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 5;        // 5분
    private final long REFRESH_TOKEN_EXPIRY = 1000 * 60 * 60 * 24; // 24시간

    // ✅ Access Token 생성
    public String createAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256) // 🔄 변경됨: signWith(String) → signWith(Key, Algo)
                .compact();
    }

    // ✅ Refresh Token 생성
    public String createRefreshToken() {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(key, SignatureAlgorithm.HS256) // 🔄 동일하게 Key 객체 사용
                .compact();
    }

    // ✅ userId 추출
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder() // 🔄 기존 parser() deprecated → parserBuilder()로 변경
                .setSigningKey(key) // 🔄 문자열 대신 Key 사용
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ 만료 여부 확인
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parserBuilder() // 🔄 parser() → parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // ✅ 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key) // 🔄 Key 객체 사용
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
