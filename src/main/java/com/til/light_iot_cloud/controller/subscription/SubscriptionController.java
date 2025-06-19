package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.CommandDownEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.enums.LinkType;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class SubscriptionController {
    @Resource
    private DeviceConnectionManager deviceConnectionManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @SubscriptionMapping
    public Flux<UpdateConfigurationEvent> updateConfigurationEvent(@ContextValue AuthContext authContext) {

        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        Long deviceId = authContext.getDevice().getId();

        return sinkEventHolder
                .getSinks(UpdateConfigurationEvent.class)
                .asFlux()
                .filter(e -> e.getDeviceId().equals(deviceId));
    }

    @SubscriptionMapping
    public Flux<CommandDownEvent> commandDownEvent(@ContextValue AuthContext authContext) {
        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        Long deviceId = authContext.getDevice().getId();

        return sinkEventHolder
                .getSinks(CommandDownEvent.class)
                .asFlux()
                .filter(e -> e.getDeviceId().equals(deviceId));
    }

}
