package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.EventSinkHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.event.CarStateReportEvent;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import com.til.light_iot_cloud.event.LightDataReportEvent;
import com.til.light_iot_cloud.event.LightStateReportEvent;
import com.til.light_iot_cloud.service.CarService;
import com.til.light_iot_cloud.service.LightService;
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
    private EventSinkHolder eventSinkHolder;

    @Resource
    private CarService carService;

    @Resource
    private LightService lightService;

    @SubscriptionMapping
    public Flux<Long> testSubscription() {
        return Flux.interval(Duration.ofSeconds(1));
    }

    @SubscriptionMapping
    public Flux<DeviceOnlineStateSwitchEvent> deviceOnlineStateSwitchEvent(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }
        return eventSinkHolder.getSinks(DeviceOnlineStateSwitchEvent.class).asFlux();
    }

    @SubscriptionMapping
    public Flux<LightState> lightStateReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Light light = lightService.getLightById(authContext.getUser().getId(), lightId);
        if (light == null) {
            throw new IllegalArgumentException("No such light");
        }

        return eventSinkHolder.getSinks(LightStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightStateReportEvent::getLightState);

    }

    @SubscriptionMapping
    public Flux<LightData> lightDataReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Light light = lightService.getLightById(authContext.getUser().getId(), lightId);
        if (light == null) {
            throw new IllegalArgumentException("No such light");
        }

        return eventSinkHolder.getSinks(LightDataReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightDataReportEvent::getLightData);

    }

    @SubscriptionMapping
    public Flux<CarState> carStateReportEvent(@ContextValue AuthContext authContext, @Argument Long carId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Car car = carService.getCarById(carId);
        if (car == null) {
            throw new IllegalArgumentException("No such car");
        }
        return eventSinkHolder.getSinks(CarStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getCarId().equals(car.getId()))
                .map(CarStateReportEvent::getCarState);
    }
}
