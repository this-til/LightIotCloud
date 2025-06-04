package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateConfigurationEvent extends ApplicationEvent implements ISinksEvent {

    DeviceType deviceType;
    Long deviceId;

    String key;
    String value;

    public UpdateConfigurationEvent(Object source, DeviceType deviceType, Long deviceId, String key, String value) {
        super(source);
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.key = key;
        this.value = value;
    }
}
