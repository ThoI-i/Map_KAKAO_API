package KAKAO_API.projected.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor  // ✅ 기본 생성자 필요
@AllArgsConstructor // ✅ 모든 필드를 포함한 생성자 필요
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String kakaoId;

    private String nickname;
    private String profileImage;

    // ✅ 필요한 생성자 추가
    public UserEntity(String kakaoId, String nickname, String profileImage) {
        this.kakaoId = kakaoId;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }
}
