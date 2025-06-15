package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UpdateConfigurationEvent implements ISinksEvent {

    Device device;

    String key;
    String value;

    public UpdateConfigurationEvent(Device device, String key, String value) {
        this.device = device;
        this.key = key;
        this.value = value;
    }
}
