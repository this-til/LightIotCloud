package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.CarState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CarStateReportEvent  implements ISinksEvent {

    Long carId;
    CarState carState;

    public CarStateReportEvent(Long carId, CarState carState) {
        this.carId = carId;
        this.carState = carState;
    }
}
