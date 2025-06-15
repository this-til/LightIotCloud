package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.OnlineState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeviceOnlineStateSwitchEvent implements ISinksEvent {

    OnlineState onlineState;
    Device device;

    public DeviceOnlineStateSwitchEvent(OnlineState onlineState, Device device) {
        this.onlineState = onlineState;
        this.device = device;
    }
}