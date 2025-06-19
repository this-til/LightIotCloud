package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.CarCommandKey;
import com.til.light_iot_cloud.enums.OperationCar;
import com.til.light_iot_cloud.event.CarStateReportEvent;
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

    @Resource
    private DeviceMutationController deviceMutationController;

    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> commandDown(Device car, @Argument CarCommandKey key, @Argument String value) {
        return deviceMutationController.commandDown(car, key.getValue(), value);
    }

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
    public Result<Void> operationCar(Device car, @Argument OperationCar operationCar) {
        return commandDown(car, CarCommandKey.OPERATION, operationCar.name());
    }


}
