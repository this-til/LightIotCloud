package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.input.OperationCarInput;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class OperationCarEvent extends ApplicationEvent implements ISinksEvent {

    Long carId;
    OperationCarInput operationCarInput;

    public OperationCarEvent(Object source, Long carId, OperationCarInput operationCarInput) {
        super(source);
        this.operationCarInput = operationCarInput;
        this.carId = carId;
    }

}
