package com.til.light_iot_cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.data.UserType;
import com.til.light_iot_cloud.service.UserService;
import com.til.light_iot_cloud.mapper.UserMapper;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author cat
 * @description 针对表【user(用户信息表)】的数据库操作Service实现
 * @createDate 2025-05-30 20:16:30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Nullable
    @Override
    public User getUserById(long id) {
        return getById(id);
    }

    @Nullable
    @Override
    public User getUserByUsername(String username) {
        return getOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
    }

    @SneakyThrows
    @Override
    public void register(String username, String password) {
        User userByUsername = getUserByUsername(username);

        if (userByUsername != null) {
            throw new Exception("用户名重复");
        }

        String encode = passwordEncoder.encode(password);

        User registerUser = new User();
        registerUser.setUsername(username);
        registerUser.setPassword(encode);
        registerUser.setUserType(UserType.user);

        save(registerUser);
    }

    @SneakyThrows
    @Override
    public String login(String username, String password) {
        User userByUsername = getUserByUsername(username);

        if (userByUsername == null) {
            throw new Exception("用户不存在");
        }

        return jwtTokenConfig.generateJwt(userByUsername);
    }

}




