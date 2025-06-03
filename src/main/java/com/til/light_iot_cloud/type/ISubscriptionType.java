package com.til.light_iot_cloud.type;

import com.til.light_iot_cloud.data.subscription.UpdateConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    ISubscriptionType<UpdateConfiguration> updateConfiguration = new SubscriptionType<>("updateConfiguration");

}
