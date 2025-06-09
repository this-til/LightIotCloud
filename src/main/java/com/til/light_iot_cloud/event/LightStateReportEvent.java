package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.LightState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LightStateReportEvent  implements ISinksEvent{

    Long lightId;
    LightState lightState;

    public LightStateReportEvent( Long lightId, LightState lightState) {
        this.lightId = lightId;
        this.lightState = lightState;
    }
}
