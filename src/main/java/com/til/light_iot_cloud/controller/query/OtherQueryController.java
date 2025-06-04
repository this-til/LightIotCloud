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
        return lightService.getLightById(deviceOnlineStateSwitchEvent.getId());
    }


    @SchemaMapping(typeName = "DeviceOnlineStateSwitchEvent")
    public Car car(DeviceOnlineStateSwitchEvent deviceOnlineStateSwitchEvent) {
        if (deviceOnlineStateSwitchEvent.getDeviceType() != DeviceType.CAR) {
            return null;
        }
        return carService.getCarById(deviceOnlineStateSwitchEvent.getId());
    }
}
