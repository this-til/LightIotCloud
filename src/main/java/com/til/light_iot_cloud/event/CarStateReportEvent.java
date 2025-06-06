package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.CarState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CarStateReportEvent extends ApplicationEvent implements ISinksEvent {

    Long carId;
    CarState carState;

    public CarStateReportEvent(Object source, Long carId, CarState carState) {
        super(source);
        this.carId = carId;
        this.carState = carState;
    }
}
