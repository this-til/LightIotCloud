package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.OnlineState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeviceOnlineStateSwitchEvent extends ApplicationEvent {

    OnlineState onlineState;
    DeviceType deviceType;
    Long id;

    public DeviceOnlineStateSwitchEvent(Object source, OnlineState onlineState, DeviceType deviceType, Long id) {
        super(source);
        this.onlineState = onlineState;
        this.deviceType = deviceType;
        this.id = id;
    }
}
