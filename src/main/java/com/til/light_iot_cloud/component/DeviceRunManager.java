package com.til.light_iot_cloud.component;


import com.til.light_iot_cloud.context.CarContext;
import com.til.light_iot_cloud.context.LightContext;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.event.EventListener;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class DeviceRunManager {

    @Resource
    private SinkEventHolder sinkEventHolder;

    private final Map<Long, LightContext> lightMap = new ConcurrentHashMap<>();
    private final Map<Long, CarContext> carMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        sinkEventHolder.getSinks(DeviceOnlineStateSwitchEvent.class)
                .asFlux()
                .subscribe(
                        event -> {
                            switch (event.getDeviceType()) {
                                case LIGHT -> {
                                    switch (event.getOnlineState()) {
                                        case ONLINE -> lightMap.put(event.getDeviceId(), new LightContext(event.getDeviceId()));
                                        case OFFLINE -> lightMap.remove(event.getDeviceId());
                                    }
                                }
                                case CAR -> {
                                    switch (event.getOnlineState()) {
                                        case ONLINE -> carMap.put(event.getDeviceId(), new CarContext(event.getDeviceId()));
                                        case OFFLINE -> carMap.remove(event.getDeviceId());
                                    }
                                }
                            }
                        }
                );
    }


    @Nullable
    public LightContext getLightContext(Long id) {
        return lightMap.get(id);
    }

    @Nullable
    public CarContext getCarContext(Long id) {
        return carMap.get(id);
    }
}
