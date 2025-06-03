package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.service.CarService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.math.MathContext;

@Controller
@SchemaMapping(typeName = "CarMutation")
public class CarMutationController {

    @Resource
    private CarService carService;


}
