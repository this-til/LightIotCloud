package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.CarState;
import com.til.light_iot_cloud.data.Device;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CarController {

    @Resource
    private DeviceRunManager deviceRunManager;


    @SchemaMapping(typeName = "Car")
    public CarState carState(Device car) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(car.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for " + car.getId());
        }

        if (!(deviceContext instanceof DeviceContext.CarContext carContext)) {
            throw new IllegalArgumentException("Device context is not a car context");
        }

        return carContext.getCarState();
    }

}
