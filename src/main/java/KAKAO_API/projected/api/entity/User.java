package KAKAO_API.projected.api.entity;

import lombok.*;

import javax.swing.*;

@Getter @Setter @ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {
    /*
    CREATE TABLE space_log (
    id VARCHAR(50) PRIMARY KEY,  -- PK로 설정
    space VARCHAR(255) NOT NULL, -- 저장된 장소
    time VARCHAR(50) NOT NULL    -- 저장된 시간
);
     */
    private Long id;
    private String space;
    private String time;
}
