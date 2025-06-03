package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.service.CarService;
import com.til.light_iot_cloud.service.LightService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller

public class UserController {

    @Resource
    private CarService carService;

    @Resource
    private LightService lightService;

    @SchemaMapping(typeName = "User")
    public List<Light> lights(User user) {
        return lightService.getLightsByUser(user.getId());
    }

    @SchemaMapping(typeName = "User")
    public List<Car> cars(User user) {
        return carService.getCarsByUser(user.getId());
    }

    @SchemaMapping(typeName = "User")
    public Light getLightByName(User user, @Argument String name) {
        return lightService.getLightByName(user.getId(), name);
    }

    @SchemaMapping(typeName = "User")
    public Car getCarByName(User user, @Argument String name) {
        return carService.getCarByName(user.getId(), name);
    }

    @SchemaMapping(typeName = "User")
    public Light getLightById(User user, @Argument Long id) {
        return lightService.getLightById(user.getId(), id);
    }

    @SchemaMapping(typeName = "User")
    public Car getCarById(User user, @Argument Long id) {
        return carService.getCarById(user.getId(), id);
    }


}
