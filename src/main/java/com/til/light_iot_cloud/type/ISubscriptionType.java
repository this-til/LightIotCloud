package com.til.light_iot_cloud.type;

import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import lombok.AllArgsConstructor;

public interface ISubscriptionType<D> {
    String name();

    @AllArgsConstructor
    class SubscriptionType<D> implements ISubscriptionType<D> {

        String name;

        @Override
        public String name() {
            return name;
        }
    }

    // -----------------------device-----------------------

    ISubscriptionType<UpdateConfigurationEvent> updateConfiguration = new SubscriptionType<>("updateConfiguration");

    // -----------------------device-----------------------

    // -----------------------web-----------------------

    ISubscriptionType<DeviceOnlineStateSwitchEvent> deviceOnlineStateSwitchEvent = new SubscriptionType<>("deviceOnlineStateSwitchEvent");

    // -----------------------web-----------------------

}
