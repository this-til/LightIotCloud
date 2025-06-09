package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
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
    private DeviceRunManager deviceRunManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> reportState(Car car, @Argument CarState carState) {
        sinkEventHolder.publishEvent(new CarStateReportEvent( car.getId(), carState));
        return Result.successful();
    }

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> setConfiguration(Car car, @Argument String key, @Argument String value) {
        sinkEventHolder.publishEvent(new UpdateConfigurationEvent( DeviceType.LIGHT, car.getId(), key, value));
        return Result.successful();
    }

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> operationCar(Car car, @Argument OperationCarInput operationCarInput) {
        OperationCarEvent operationCarEvent = new OperationCarEvent( car.getId(), operationCarInput);
        sinkEventHolder.publishEvent(operationCarEvent);
        return Result.successful();
    }

}
