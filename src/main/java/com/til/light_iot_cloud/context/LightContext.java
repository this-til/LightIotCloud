package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.data.LightState;
import lombok.Data;

@Data
public class LightContext  {

    Long id;
    LightState lightState;

    public LightContext(Long id) {
        this.id = id;
    }

}
