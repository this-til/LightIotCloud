package com.til.light_iot_cloud.service;

import com.til.light_iot_cloud.data.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nullable;

/**
 * @author cat
 * @description 针对表【user(用户信息表)】的数据库操作Service
 * @createDate 2025-05-30 20:16:30
 */
public interface UserService extends IService<User> {

    @Nullable
    User getUserById(long id);

    @Nullable
    User getUserByUsername(String username);

    void register(String username, String password);

    /***
     * 登录让后生成JWT令牌
     */
    String login(String username, String password);

}
