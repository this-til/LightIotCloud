package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.event.OperationCarEvent;
import com.til.light_iot_cloud.service.CarService;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.math.MathContext;

@Controller
public class CarMutationController {

    @Resource
    private CarService carService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> operationCar(Car car, @Argument OperationCarInput operationCarInput) {
        OperationCarEvent operationCarEvent = new OperationCarEvent(this, car.getId(), operationCarInput);
        eventPublisher.publishEvent(operationCarEvent);
        return Result.successful();
    }

}
