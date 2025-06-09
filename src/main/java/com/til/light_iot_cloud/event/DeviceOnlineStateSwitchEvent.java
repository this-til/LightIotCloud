package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.OnlineState;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DeviceOnlineStateSwitchEvent implements ISinksEvent {

    OnlineState onlineState;
    DeviceType deviceType;
    Long deviceId;

    public DeviceOnlineStateSwitchEvent(OnlineState onlineState, DeviceType deviceType, Long deviceId) {
        this.onlineState = onlineState;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

}