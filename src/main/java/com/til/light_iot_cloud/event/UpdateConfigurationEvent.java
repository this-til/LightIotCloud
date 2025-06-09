package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateConfigurationEvent implements ISinksEvent {

    DeviceType deviceType;
    Long deviceId;

    String key;
    String value;

    public UpdateConfigurationEvent(DeviceType deviceType, Long deviceId, String key, String value) {
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.key = key;
        this.value = value;
    }
}
