package KAKAO_API.projected.api.service;

import KAKAO_API.projected.api.entity.User;
import KAKAO_API.projected.api.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

//    @Autowired
//    public UserService(UserMapper userMapper) {
//        this.userMapper = userMapper;
//    }

    public void createProduct(User user) {
        userMapper.save(user);
    }

    public User getProductById(Long id) {
        return userMapper.findById(id);
    }

    public List<User> getAllProducts() {
        return userMapper.findAll();
    }

    public void updateProduct(User user) {
        userMapper.update(user);
    }

    public void deleteProduct(Long id) {
        userMapper.deleteById(id);
    }
}

