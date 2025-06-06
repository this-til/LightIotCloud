package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.EventSinkHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.OperationCarEvent;
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
    private EventSinkHolder eventSinkHolder;

    @SubscriptionMapping
    public Flux<UpdateConfigurationEvent> updateConfigurationEvent(@ContextValue AuthContext authContext) {

        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        DeviceType deviceType = authContext.getDeviceType();
        Long deviceId = authContext.getDeviceId();
        return eventSinkHolder
                .getSinks(UpdateConfigurationEvent.class)
                .asFlux()
                .filter(e -> e.getDeviceType().equals(deviceType))
                .filter(e -> e.getDeviceId().equals(deviceId));
    }

    @SubscriptionMapping
    public Flux<OperationCarInput> operationCarEvent(@ContextValue AuthContext authContext) {
        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        DeviceType deviceType = authContext.getDeviceType();
        if (deviceType != DeviceType.CAR) {
            throw new IllegalArgumentException("Unsupported device type: " + deviceType);
        }

        Long id = authContext.getCar().getId();
        return eventSinkHolder
                .getSinks(OperationCarEvent.class)
                .asFlux()
                .filter(e -> e.getCarId().equals(id))
                .map(OperationCarEvent::getOperationCarInput);
    }



}
