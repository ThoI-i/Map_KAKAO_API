package KAKAO_API.projected.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // 🔐 Refresh 토큰 저장 (TTL: 24시간)
    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(getRefreshKey(userId), refreshToken, 24, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(getRefreshTimeKey(userId), String.valueOf(System.currentTimeMillis()), 24, TimeUnit.HOURS);
    }

    // 🧾 Refresh 토큰 조회
    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get(getRefreshKey(userId));
    }

    // 🕓 마지막 저장된 시간 조회
    public long getLastSavedTime(String userId) {
        String timeStr = redisTemplate.opsForValue().get(getRefreshTimeKey(userId));
        return timeStr == null ? 0L : Long.parseLong(timeStr);
    }

    // ❌ Refresh 토큰 삭제 (로그아웃 시 사용 예정)
    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(getRefreshKey(userId));
        redisTemplate.delete(getRefreshTimeKey(userId));
    }

    // 📛 내부 키 네이밍 규칙
    private String getRefreshKey(String userId) {
        return "refresh:" + userId;
    }

    private String getRefreshTimeKey(String userId) {
        return "refresh_time:" + userId;
    }
}
