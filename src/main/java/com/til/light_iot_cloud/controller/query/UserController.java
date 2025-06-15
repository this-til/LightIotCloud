package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.service.DetectionModelService;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller

public class UserController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private DetectionModelService detectionModelService;

    @SchemaMapping(typeName = "User")
    public List<DetectionModel> model(User user) {
        return detectionModelService.list(
                new LambdaQueryWrapper<DetectionModel>()
                        .eq(DetectionModel::getUserId, user.getId())
        );
    }

    @SchemaMapping(typeName = "User")
    public List<Device> devices(User user, @Argument DeviceType deviceType) {
        return deviceService.list(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, user.getId())
                        .eq(Device::getDeviceType, deviceType)
        );
    }

    @Nullable
    @SchemaMapping(typeName = "User")
    public Device getDeviceByName(User user, @Argument String name, @Argument DeviceType deviceType) {
        return deviceService.getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, user.getId())
                        .eq(Device::getDeviceType, deviceType)
                        .eq(Device::getName, name)
        );
    }

    @Nullable
    @SchemaMapping(typeName = "User")
    public Device getDeviceById(User user, @Argument Long id, @Argument DeviceType deviceType) {
        return deviceService.getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, user.getId())
                        .eq(Device::getDeviceType, deviceType)
                        .eq(Device::getId, id)
        );
    }
}
