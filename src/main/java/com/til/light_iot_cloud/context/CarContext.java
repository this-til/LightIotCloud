package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.data.CarState;
import lombok.Data;

@Data
public class CarContext {
    Long id;
    CarState carState;

    public CarContext(Long id) {
        this.id = id;
    }
}
