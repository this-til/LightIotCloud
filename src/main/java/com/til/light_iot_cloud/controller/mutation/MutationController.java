package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.enums.CarCommandKey;
import com.til.light_iot_cloud.enums.LightCommandKey;
import com.til.light_iot_cloud.service.DeviceService;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 主变更控制器
 * <p>
 * 提供系统的核心变更操作，包括用户认证、全局设备管理和系统级命令调度。
 * 作为 GraphQL 变更操作的主入口，集成了用户登录注册、JWT 验证、
 * 设备信息获取以及全局设备调度等核心功能。
 * <p>
 * 主要功能：
 * - 用户身份认证（登录、注册）
 * - JWT 令牌管理和验证
 * - 用户和设备信息查询
 * - 全局设备调度命令（调度、中断、结束调度）
 * <p>
 * 调度机制：
 * - 支持对所有在线设备进行统一调度
 * - 区分灯光设备和车辆设备的不同命令
 * - 提供调度、中断、结束调度三种全局操作
 * 
 * @author TIL
 */
@Controller
public class MutationController {

    @Resource
    private UserService userService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Resource
    private LightMutationController lightMutationController;

    @Resource
    private CarMutationController carMutationController;

    @Resource
    private DeviceRunManager deviceRunManager;

    /**
     * 用户登录
     * <p>
     * 验证用户名和密码，成功后返回 JWT 令牌用于后续的身份认证。
     * 令牌包含用户ID和过期时间等信息。
     * 
     * @param username 用户名
     * @param password 密码（明文）
     * @return JWT 令牌字符串，用于后续API调用的身份验证
     * @throws SecurityException 当用户名或密码错误时抛出
     */
    @MutationMapping()
    public String login(@Argument String username, @Argument String password) {
        return userService.login(username, password);
    }

    /**
     * 用户注册
     * <p>
     * 创建新的用户账户。用户名必须唯一，密码将被加密存储。
     * 注册成功后用户可以使用新账户进行登录。
     * 
     * @param username 用户名，必须唯一
     * @param password 密码（明文），系统将自动加密
     * @return 注册是否成功
     * @throws IllegalArgumentException 当用户名已存在或格式不正确时抛出
     */
    @MutationMapping()
    public boolean register(@Argument String username, @Argument String password) {
        userService.register(username, password);
        return true;
    }

    /**
     * JWT 令牌有效性验证
     * <p>
     * 验证提供的 JWT 令牌是否有效且未过期。
     * 用于前端检查用户登录状态或令牌是否需要刷新。
     * 
     * @param jwt JWT 令牌字符串
     * @return 令牌是否有效
     */
    @MutationMapping
    public boolean jwtEffective(@Argument String jwt) {
        try {
            jwtTokenConfig.parseJwt(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取当前用户信息
     * <p>
     * 根据认证上下文返回当前登录用户的详细信息。
     * 需要有效的用户身份认证。
     * 
     * @param authContext 认证上下文，包含当前用户信息
     * @return 当前用户对象
     * @throws SecurityException 当用户未登录时抛出
     */
    @MutationMapping
    public User self(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            throw new SecurityException("You are not logged in");
        }
        return authContext.getUser();
    }

    /**
     * 获取当前设备信息
     * <p>
     * 当以设备身份进行认证时，返回当前设备的详细信息。
     * 主要用于设备端API调用时获取自身信息。
     * 
     * @param authContext 认证上下文，必须包含设备信息
     * @return 当前设备对象
     * @throws SecurityException 当用户未登录或登录者不是设备时抛出
     */
    @MutationMapping
    public Device deviceSelf(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            throw new SecurityException("You are not logged in");
        }
        if (authContext.getDevice() == null) {
            throw new SecurityException("The logged is not device");
        }
        return authContext.getDevice();
    }

    /**
     * 全局设备调度
     * <p>
     * 向所有在线的灯光设备和车辆设备发送调度命令，
     * 启动设备的任务执行模式。这是一个系统级的全局操作。
     * <p>
     * 调度范围：
     * - 所有在线的灯光设备
     * - 所有在线的车辆设备
     * 
     * @return 操作结果，成功时返回 successful
     */
    @MutationMapping
    public Result<Void> dispatch() {
        Map<Long, DeviceContext> deviceContextMap = deviceRunManager.getDeviceContextMap();

        deviceContextMap.values().stream()
                .filter(v -> v instanceof DeviceContext.LightContext)
                .map(v -> (DeviceContext.LightContext) v)
                .forEach(v -> lightMutationController.commandDown(v.getDevice(), LightCommandKey.DISPATCH, "null"));

        deviceContextMap.values().stream()
                .filter(v -> v instanceof DeviceContext.CarContext)
                .map(v -> (DeviceContext.CarContext) v)
                .forEach(v -> carMutationController.commandDown(v.getDevice(), CarCommandKey.DISPATCH, "null"));

        return Result.successful();
    }

    /**
     * 全局设备中断
     * <p>
     * 向所有在线的灯光设备和车辆设备发送中断命令，
     * 暂停设备当前的任务执行。这是一个紧急中断操作。
     * <p>
     * 中断范围：
     * - 所有在线的灯光设备
     * - 所有在线的车辆设备
     * 
     * @return 操作结果，成功时返回 successful
     */
    @MutationMapping
    public Result<Void> interrupt() {
        Map<Long, DeviceContext> deviceContextMap = deviceRunManager.getDeviceContextMap();

        deviceContextMap.values().stream()
                .filter(v -> v instanceof DeviceContext.LightContext)
                .map(v -> (DeviceContext.LightContext) v)
                .forEach(v -> lightMutationController.commandDown(v.getDevice(), LightCommandKey.INTERRUPT, "null"));

        deviceContextMap.values().stream()
                .filter(v -> v instanceof DeviceContext.CarContext)
                .map(v -> (DeviceContext.CarContext) v)
                .forEach(v -> carMutationController.commandDown(v.getDevice(), CarCommandKey.INTERRUPT, "null"));

        return Result.successful();
    }

    /**
     * 结束全局调度
     * <p>
     * 向所有在线的灯光设备和车辆设备发送结束调度命令，
     * 正常结束设备的任务执行并返回待机状态。
     * <p>
     * 结束调度范围：
     * - 所有在线的灯光设备
     * - 所有在线的车辆设备
     * 
     * @return 操作结果，成功时返回 successful
     */
    @MutationMapping
    public Result<Void> endDispatch() {
        Map<Long, DeviceContext> deviceContextMap = deviceRunManager.getDeviceContextMap();

        deviceContextMap.values().stream()
                .filter(v -> v instanceof DeviceContext.LightContext)
                .map(v -> (DeviceContext.LightContext) v)
                .forEach(v -> lightMutationController.commandDown(v.getDevice(), LightCommandKey.END_DISPATCH, "null"));

        deviceContextMap.values().stream()
                .filter(v -> v instanceof DeviceContext.CarContext)
                .map(v -> (DeviceContext.CarContext) v)
                .forEach(v -> carMutationController.commandDown(v.getDevice(), CarCommandKey.END_DISPATCH, "null"));

        return Result.successful();
    }

}
