package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.EventSinkHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.context.Publisher;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.type.ISubscriptionType;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Controller
public class SubscriptionController {
    @Resource
    private DeviceConnectionManager deviceConnectionManager;

    @Resource
    private EventSinkHolder eventSinkHolder;

    @SubscriptionMapping
    public Flux<UpdateConfigurationEvent> updateConfiguration(@ContextValue AuthContext authContext) {

        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        return eventSinkHolder
                .getSinks(UpdateConfigurationEvent.class)
                .asFlux()
                .filter(e -> e.getDeviceType().equals(authContext.getDeviceType()))
                .filter(e -> e.getDeviceId().equals(authContext.getDeviceId()));
    }
}
