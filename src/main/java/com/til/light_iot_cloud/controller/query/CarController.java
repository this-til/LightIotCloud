package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.CarContext;
import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.data.CarState;
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

    @SchemaMapping(typeName = "Car")
    public CarState carState(Car car) {
        CarContext carContext = deviceRunManager.getCarContext(car.getId());
        if (carContext == null) {
            return null;
        }
        return carContext.getCarState();
    }

}
