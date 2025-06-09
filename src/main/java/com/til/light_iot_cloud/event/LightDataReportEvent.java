package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.LightData;
import com.til.light_iot_cloud.data.LightState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LightDataReportEvent implements ISinksEvent {
    Long lightId;
    LightData lightData;

    public LightDataReportEvent(Long lightId, LightData lightData) {
        this.lightId = lightId;
        this.lightData = lightData;
    }
}
