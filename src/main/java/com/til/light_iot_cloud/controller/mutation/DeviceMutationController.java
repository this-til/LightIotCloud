package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.CommandDownEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DeviceMutationController {
    @Resource
    private DeviceService deviceService;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @SchemaMapping(typeName = "DeviceMutation")
    public Result<Void> setConfiguration(Device device, @Argument String key, @Argument String value) {
        sinkEventHolder.publishEvent(new UpdateConfigurationEvent(device.getId(), key, value));
        return Result.successful();
    }

    @SchemaMapping(typeName = "DeviceMutation")
    public Result<Void> commandDown(Device device, @Argument String key, @Argument String value) {
        sinkEventHolder.publishEvent(new CommandDownEvent(device.getId(), key, value));
        return Result.successful();
    }

    @SchemaMapping(typeName = "DeviceMutation")
    public Device asLight(Device device) {
        if (!device.getDeviceType().equals(DeviceType.LIGHT)) {
            throw new IllegalArgumentException("Device type is not Light");
        }
        return device;
    }

    @SchemaMapping(typeName = "DeviceMutation")
    public Device asCarMutation(Device device) {
        if (!device.getDeviceType().equals(DeviceType.CAR)) {
            throw new IllegalArgumentException("Device type is not Car");
        }
        return device;
    }
}
