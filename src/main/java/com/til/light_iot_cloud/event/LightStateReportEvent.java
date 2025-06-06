package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.LightState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LightStateReportEvent extends ApplicationEvent implements ISinksEvent{

    Long lightId;
    LightState lightState;

    public LightStateReportEvent(Object source, Long lightId, LightState lightState) {
        super(source);
        this.lightId = lightId;
        this.lightState = lightState;
    }
}
