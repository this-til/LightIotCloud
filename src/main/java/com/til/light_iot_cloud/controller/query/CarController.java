package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.service.CarService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CarController {

    @Resource
    private CarService carService;

    @Resource
    private DeviceRunManager deviceRunManager;

    @SchemaMapping(typeName = "Car")
    public boolean online(Car car) {
        return deviceRunManager.getCarContext(car.getId()) != null;
    }

}
