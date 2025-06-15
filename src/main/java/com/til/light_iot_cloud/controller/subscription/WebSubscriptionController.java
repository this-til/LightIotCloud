package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.event.*;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class WebSubscriptionController {

    @Resource
    private SinkEventHolder sinkEventHolder;

    @Resource
    private DeviceService deviceService;


    @SubscriptionMapping
    public Flux<Long> testSubscription() {
        return Flux.interval(Duration.ofSeconds(1));
    }

    @SubscriptionMapping
    public Flux<DeviceOnlineStateSwitchEvent> deviceOnlineStateSwitchEvent(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }
        return sinkEventHolder.getSinks(DeviceOnlineStateSwitchEvent.class).asFlux();
    }

    @SubscriptionMapping
    public Flux<LightState> lightStateReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }

        return sinkEventHolder.getSinks(LightStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightStateReportEvent::getLightState);

    }

    @SubscriptionMapping
    public Flux<LightData> lightDataReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }


        return sinkEventHolder.getSinks(LightDataReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightDataReportEvent::getLightData);

    }

    @SubscriptionMapping
    public Flux<DetectionKeyframe> lightDetectionReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {

        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }


        return sinkEventHolder.getSinks(LightDetectionReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightDetectionReportEvent::getDetectionKeyframe);

    }

    @SubscriptionMapping
    public Flux<CarState> carStateReportEvent(@ContextValue AuthContext authContext, @Argument Long carId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), carId, DeviceType.CAR);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }

        return sinkEventHolder.getSinks(CarStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getCarId().equals(carId))
                .map(CarStateReportEvent::getCarState);
    }
}
