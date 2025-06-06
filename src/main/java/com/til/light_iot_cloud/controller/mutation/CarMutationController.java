package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.context.CarContext;
import com.til.light_iot_cloud.context.LightContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.CarStateReportEvent;
import com.til.light_iot_cloud.event.OperationCarEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.service.CarService;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CarMutationController {

    @Resource
    private CarService carService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Resource
    private DeviceRunManager deviceRunManager;

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> reportState(Car car, @Argument CarState carState) {
        eventPublisher.publishEvent(new CarStateReportEvent(this, car.getId(), carState));
        return Result.successful();
    }


    @SchemaMapping(typeName = "Light")
    public CarState lightState(@ContextValue AuthContext authContext, Car car) {
        CarContext carContext = deviceRunManager.getCarContext(car.getId());
        if (carContext == null) {
            return null;

        }
        return carContext.getCarState();
    }

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> setConfiguration(Car car, @Argument String key, @Argument String value) {
        eventPublisher.publishEvent(new UpdateConfigurationEvent(this, DeviceType.LIGHT, car.getId(), key, value));
        return Result.successful();
    }

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> operationCar(Car car, @Argument OperationCarInput operationCarInput) {
        OperationCarEvent operationCarEvent = new OperationCarEvent(this, car.getId(), operationCarInput);
        eventPublisher.publishEvent(operationCarEvent);
        return Result.successful();
    }

}
