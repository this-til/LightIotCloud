package com.til.light_iot_cloud.controller.mutation;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.controller.query.UserController;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.service.DeviceService;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 用户变更控制器
 * <p>
 * 提供用户相关的设备查询和管理功能，作为 GraphQL UserMutation 类型的实现。
 * 主要负责用户权限范围内的设备查询操作，确保用户只能访问属于自己的设备。
 * <p>
 * 主要功能：
 * - 查询用户拥有的所有设备
 * - 按设备类型筛选设备列表
 * - 根据设备名称查找设备
 * - 根据设备ID查找设备
 * <p>
 * 权限控制：
 * - 所有查询都限制在当前用户的设备范围内
 * - 支持多种设备类型的统一管理
 * - 提供精确的设备定位功能
 * 
 * @author TIL
 */
@Controller
public class UserMutationController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private UserService userService;

    @Resource
    private UserController userController;

    /**
     * 获取用户设备列表
     * <p>
     * 查询当前用户拥有的所有设备，可以按设备类型进行筛选。
     * 如果不指定设备类型，则返回用户的所有设备。
     * 
     * @param user 当前用户对象，由 GraphQL 上下文提供
     * @param deviceType 设备类型筛选条件，可选参数
     *                   - 如果为空，返回所有类型的设备
     *                   - 如果指定，只返回该类型的设备
     * @return 设备列表，按查询条件筛选后的结果
     */
    @SchemaMapping(typeName = "UserMutation")
    public List<Device> devices(User user, @Nullable @Argument DeviceType deviceType) {
        LambdaQueryWrapper<Device> eq = new LambdaQueryWrapper<Device>()
                .eq(Device::getUserId, user.getId());
        if (deviceType != null) {
            eq.eq(Device::getDeviceType, deviceType);
        }
        return deviceService.list(eq);
    }

    /**
     * 根据设备名称查找设备
     * <p>
     * 在当前用户的设备中根据设备名称和类型查找特定设备。
     * 由于设备名称在同一用户和同一类型下应该是唯一的，
     * 此方法返回单个设备或null。
     * 
     * @param user 当前用户对象
     * @param name 设备名称，精确匹配
     * @param deviceType 设备类型，用于进一步限制查找范围
     * @return 匹配的设备对象，如果未找到则返回null
     */
    @Nullable
    @SchemaMapping(typeName = "UserMutation")
    public Device getDeviceByName(User user, @Argument String name, @Argument DeviceType deviceType) {
        return deviceService.getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, user.getId())
                        .eq(Device::getDeviceType, deviceType)
                        .eq(Device::getName, name)
        );
    }

    /**
     * 根据设备ID查找设备
     * <p>
     * 在当前用户的设备中根据设备ID和类型查找特定设备。
     * 此方法提供设备的精确定位，同时验证设备所有权。
     * 
     * @param user 当前用户对象
     * @param id 设备ID，唯一标识符
     * @param deviceType 设备类型，用于验证设备类型的正确性
     * @return 匹配的设备对象，如果未找到或无权限则返回null
     */
    @Nullable
    @SchemaMapping(typeName = "UserMutation")
    public Device getDeviceById(User user, @Argument Long id, @Argument DeviceType deviceType) {
        return deviceService.getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, user.getId())
                        .eq(Device::getDeviceType, deviceType)
                        .eq(Device::getId, id)
        );
    }

}

