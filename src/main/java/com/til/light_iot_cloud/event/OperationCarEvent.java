package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.input.OperationCarInput;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public class OperationCarEvent extends ApplicationEvent implements ISinksEvent {

    Long carId;
    OperationCarInput operationCarInput;

    Boolean translationAdvance;
    Boolean translationLeft;
    Boolean translationRetreat;
    Boolean translationRight;
    Boolean angularLeft;
    Boolean angularRight;
    Boolean stop;

    public OperationCarEvent(Object source, Long carId, OperationCarInput operationCarInput) {
        super(source);
        this.operationCarInput = operationCarInput;
        this.carId = carId;

        translationAdvance = operationCarInput.getTranslationAdvance();
        translationLeft = operationCarInput.getTranslationLeft();
        translationRetreat = operationCarInput.getTranslationRetreat();
        translationRight = operationCarInput.getTranslationRight();
        angularLeft = operationCarInput.getAngularLeft();
        angularRight = operationCarInput.getAngularRight();
        stop = operationCarInput.getStop();
    }

}
