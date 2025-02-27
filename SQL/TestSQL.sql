CREATE TABLE space_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- Long 타입 PK (자동 증가)
    space VARCHAR(255) NOT NULL, -- 저장된 장소
    time VARCHAR(50) NOT NULL    -- 저장된 시간
);


INSERT INTO space_log (id, space, time) 
VALUES (1, '스타벅스 강남점', NOW());


SELECT * FROM space_log;