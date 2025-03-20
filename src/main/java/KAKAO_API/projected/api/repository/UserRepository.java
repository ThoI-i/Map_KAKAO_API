package KAKAO_API.projected.api.repository;

import KAKAO_API.projected.api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    // ✅ Kakao ID로 사용자 조회 (Optional 반환)
    Optional<UserEntity> findByKakaoId(String kakaoId);
}
