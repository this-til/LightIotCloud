package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * 主查询控制器
 * 
 * 提供系统的核心查询操作，包括用户信息和设备信息的基本查询功能。
 * 作为 GraphQL 查询操作的主入口，负责处理身份验证相关的查询。
 * 
 * 主要功能：
 * - 用户身份信息查询
 * - 设备身份信息查询
 * - 临时用户认证（已废弃）
 * 
 * 认证机制：
 * - 支持用户和设备两种认证模式
 * - 验证连接类型和权限
 * - 提供安全的身份信息获取
 * 
 * @author TIL
 */
@Controller
public class QueryController {

    @Resource
    public UserService userService;

    /**
     * 临时用户认证
     * 
     * @deprecated 此方法已废弃，建议使用标准的登录流程
     * 
     * 提供临时的用户认证功能，用于快速验证用户身份。
     * 此方法绕过了标准的JWT认证流程，仅用于开发和测试环境。
     * 
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，如果认证失败则为null
     */
    @SuppressWarnings("SpringGraphQLUnmatchedMappingInspection")
    @Deprecated
    @QueryMapping
    public User temporary(@Argument String username, @Argument String password) {
        return userService.temporary(username, password);
    }

    /**
     * 获取当前登录用户信息
     * 
     * 根据认证上下文返回当前登录用户的详细信息。
     * 这是获取用户自身信息的标准方法。
     * 
     * @param authContext 认证上下文，包含当前用户信息
     * @return 当前用户对象，如果未登录则返回null
     */
    @QueryMapping
    public User self(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            return null;
        }
        return authContext.getUser();
    }

    /**
     * 获取当前设备信息
     * 
     * 当以设备身份进行认证时，返回当前设备的详细信息。
     * 仅限设备WebSocket连接使用，用于设备获取自身信息。
     * 
     * 连接要求：
     * - 必须是设备WebSocket连接
     * - 必须具有有效的设备认证上下文
     * 
     * @param authContext 认证上下文，必须包含设备信息
     * @return 当前设备对象，如果不是设备连接或认证无效则返回null
     */
    @QueryMapping
    public Device deviceSelf(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.DEVICE_WEBSOCKET) {
            return null;
        }
        return authContext.getDevice();
    }

}
