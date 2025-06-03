package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.controller.query.UserController;
import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.data.Light;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.service.CarService;
import com.til.light_iot_cloud.service.LightService;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserMutationController {

    @Resource
    private CarService carService;

    @Resource
    private LightService lightService;

    @Resource
    private UserService userService;

    @Resource
    private UserController userController;

    @SchemaMapping(typeName = "UserMutation")
    public List<Light> lights(User user) {
        return userController.lights(user);
    }

    @SchemaMapping(typeName = "UserMutation")
    public List<Car> cars(User user) {
        return userController.cars(user);
    }

    @SchemaMapping(typeName = "UserMutation")
    public Light getLightByName(User user, @Argument String name) {
        return userController.getLightByName(user, name);
    }

    @SchemaMapping(typeName = "UserMutation")
    public Car getCarByName(User user, @Argument String name) {
        return userController.getCarByName(user, name);
    }

    @SchemaMapping(typeName = "UserMutation")
    public Light getLightById(User user, @Argument Long id) {
        return userController.getLightById(user, id);
    }

    @SchemaMapping(typeName = "UserMutation")
    public Car getCarById(User user, @Argument Long id) {
        return userController.getCarById(user, id);
    }

    @SchemaMapping(typeName = "UserMutation")
    public Light existLight(User user, @Argument String name) {
        return lightService.existLight(user.getId(), name);
    }

    @SchemaMapping(typeName = "UserMutation")
    public Car existCar(User user, @Argument String name) {
        return carService.existCar(user.getId(), name);
    }

}

