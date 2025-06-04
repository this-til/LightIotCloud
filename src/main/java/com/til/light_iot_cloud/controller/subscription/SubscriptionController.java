package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.context.Publisher;
import com.til.light_iot_cloud.data.subscription.UpdateConfiguration;
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

    @SubscriptionMapping
    public Flux<UpdateConfiguration> updateConfiguration(@ContextValue AuthContext authContext) {

        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.WEBSOCKET_CAR && linkType != LinkType.WEBSOCKET_LIGHT) {
            throw new IllegalArgumentException("Invalid link type: " + linkType);
        }

        Publisher publisherBySession = deviceConnectionManager.getPublisherBySession(authContext.getWebSocketSession().getId());

        if (publisherBySession == null) {
            throw new IllegalArgumentException("No publisher found for session: " + authContext.getWebSocketSession().getId());
        }

        Sinks.Many<UpdateConfiguration> updateConfigurationMany = publisherBySession.existSubscription(ISubscriptionType.updateConfiguration);

        return updateConfigurationMany
                .asFlux()
                .doOnCancel(() -> publisherBySession.unregisterSubscription(ISubscriptionType.updateConfiguration));
    }
}
