package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.EventSinkHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class WebSubscriptionController {
    @Resource
    private EventSinkHolder eventSinkHolder;

    @SubscriptionMapping
    public Flux<String> testSubscription() {
        return Flux.interval(Duration.ofSeconds(1)).map(Object::toString);
    }

    @SubscriptionMapping
    public Flux<DeviceOnlineStateSwitchEvent> deviceOnlineStateSwitchEvent(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }
        return eventSinkHolder.getSinks(DeviceOnlineStateSwitchEvent.class).asFlux();
    }

}
