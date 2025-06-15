package com.til.light_iot_cloud.component;


import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class DeviceRunManager {

    @Resource
    private SinkEventHolder sinkEventHolder;

    private final Map<Long, DeviceContext> deviceContextMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        sinkEventHolder.getSinks(DeviceOnlineStateSwitchEvent.class)
                .asFlux()
                .subscribe(
                        event -> deviceContextMap.put(event.getDevice().getId(), DeviceContext.create(event.getDevice()))
                );
    }


    @Nullable
    public DeviceContext getDeviceContext(Long id) {
        return deviceContextMap.get(id);
    }
}
