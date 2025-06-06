package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.data.Light;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import com.til.light_iot_cloud.service.CarService;
import com.til.light_iot_cloud.service.LightService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class OtherQueryController {

    @Resource
    private LightService lightService;
    @Resource
    public CarService carService;

    @SchemaMapping(typeName = "DeviceOnlineStateSwitchEvent")
    public Light light(DeviceOnlineStateSwitchEvent deviceOnlineStateSwitchEvent) {
        if (deviceOnlineStateSwitchEvent.getDeviceType() != DeviceType.LIGHT) {
            return null;
        }
        return lightService.getLightById(deviceOnlineStateSwitchEvent.getDeviceId());
    }


    @SchemaMapping(typeName = "DeviceOnlineStateSwitchEvent")
    public Car car(DeviceOnlineStateSwitchEvent deviceOnlineStateSwitchEvent) {
        if (deviceOnlineStateSwitchEvent.getDeviceType() != DeviceType.CAR) {
            return null;
        }
        return carService.getCarById(deviceOnlineStateSwitchEvent.getDeviceId());
    }

    @SchemaMapping(typeName = "DeviceOnlineStateSwitchEvent")
    public String deviceName(DeviceOnlineStateSwitchEvent deviceOnlineStateSwitchEvent) {
        return switch (deviceOnlineStateSwitchEvent.getDeviceType()) {
            case LIGHT -> lightService.getById(deviceOnlineStateSwitchEvent.getDeviceId()).getName();
            case CAR -> carService.getById(deviceOnlineStateSwitchEvent.getDeviceId()).getName();
        };
    }
}
