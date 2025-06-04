package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.subscription.UpdateConfiguration;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class WebSubscriptionController {


    @SubscriptionMapping
    public Flux<DeviceOnlineStateSwitchEvent> deviceOnlineStateSwitchEvent(@ContextValue AuthContext authContext) {

    }

}
