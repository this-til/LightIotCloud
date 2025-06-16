package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.CarStateReportEvent;
import com.til.light_iot_cloud.event.OperationCarEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CarMutationController {


    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> reportState(Device car, @Argument CarState carState) {

        DeviceContext deviceContext = deviceRunManager.getDeviceContext(car.getId());

        if (deviceContext == null) {
            throw new RuntimeException("Device context not found");
        }

        if (!(deviceContext instanceof DeviceContext.CarContext carContext)) {
            throw new RuntimeException("Device context is not a car context");
        }

        carContext.setCarState(carState);
        sinkEventHolder.publishEvent(new CarStateReportEvent(car.getId(), carState));
        return Result.successful();
    }

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> operationCar(Device car, @Argument OperationCarInput operationCarInput) {

        DeviceContext deviceContext = deviceRunManager.getDeviceContext(car.getId());

        if (deviceContext == null) {
            throw new RuntimeException("Device context not found");
        }

        if (!(deviceContext instanceof DeviceContext.CarContext carContext)) {
            throw new RuntimeException("Device context is not a car context");
        }

        OperationCarEvent operationCarEvent = new OperationCarEvent(car.getId(), operationCarInput);
        sinkEventHolder.publishEvent(operationCarEvent);
        return Result.successful();
    }

}
