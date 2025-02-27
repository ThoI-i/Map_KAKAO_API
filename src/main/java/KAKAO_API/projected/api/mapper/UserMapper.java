package KAKAO_API.projected.api.mapper;

import KAKAO_API.projected.api.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    // ✅ CREATE (삽입)
    @Insert("""
        INSERT INTO space_log
            (id, space, time)
        VALUES
            (#{id}, #{space}, #{time})
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id") // 자동 증가된 PK 반영됨
    void save(User user); // 🔥 메서드 선언

    // ✅ READ (단일 조회)
    @Select("SELECT * FROM space_log WHERE id = #{id}")
    User findById(Long id); // 🔥 메서드 선언 (단일 객체 반환)

    // ✅ READ (전체 조회)
    @Select("SELECT * FROM space_log")
    List<User> findAll(); // 🔥 메서드 선언 (리스트 반환)

    // ✅ UPDATE (수정)
    @Update("""
        UPDATE space_log
        SET id = #{id}, space = #{space}, time = #{time}
        WHERE id = #{id}
        """)
    void update(User user); // 🔥 메서드 선언 (수정 실행)

    // ✅ DELETE (삭제)
    @Delete("DELETE FROM space_log WHERE id = #{id}")
    void deleteById(Long id); // 🔥 메서드 선언 (삭제 실행)
}


