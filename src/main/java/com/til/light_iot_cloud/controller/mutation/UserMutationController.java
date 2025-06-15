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

@Controller
public class UserMutationController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private UserService userService;

    @Resource
    private UserController userController;


    @SchemaMapping(typeName = "UserMutation")
    public List<Device> devices(User user, @Argument DeviceType deviceType) {
        return deviceService.list(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, user.getId())
                        .eq(Device::getDeviceType, deviceType)
        );
    }

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

