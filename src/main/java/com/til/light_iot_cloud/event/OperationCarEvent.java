package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.input.OperationCarInput;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class OperationCarEvent implements ISinksEvent {

    Long carId;
    OperationCarInput operationCarInput;

    public OperationCarEvent(Long carId, OperationCarInput operationCarInput) {
        this.operationCarInput = operationCarInput;
        this.carId = carId;
    }

}
