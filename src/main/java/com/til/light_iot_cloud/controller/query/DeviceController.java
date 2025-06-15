package com.til.light_iot_cloud.controller.query;


import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private DeviceRunManager deviceRunManager;

    @SchemaMapping(typeName = "Device")
    public boolean online(Device device) {
        return deviceRunManager.getDeviceContext(device.getId()) != null;
    }

    @SchemaMapping(typeName = "Device")
    public Device asCar(Device device) {
        if (device.getDeviceType() != DeviceType.CAR) {
            throw new IllegalArgumentException("Device type is not Car");
        }
        return device;
    }

    @SchemaMapping(typeName = "Device")
    public Device asLight(Device device) {
        if (device.getDeviceType() != DeviceType.LIGHT) {
            throw new IllegalArgumentException("Device type is not Light");
        }
        return device;
    }

}
