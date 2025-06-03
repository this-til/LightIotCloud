package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.WebSocketConnectionManager;
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
    private WebSocketConnectionManager webSocketConnectionManager;

    @SchemaMapping(typeName = "Car")
    public boolean online(Car car) {
        return webSocketConnectionManager.getPublisherByCarId(car.getId()) != null;
    }

}
