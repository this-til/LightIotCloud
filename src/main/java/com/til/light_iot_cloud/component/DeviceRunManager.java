package com.til.light_iot_cloud.component;


import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class DeviceRunManager {

    @Resource
    private SinkEventHolder sinkEventHolder;

    @Resource
    private DeviceService deviceService;

    @Getter
    private final Map<Long, DeviceContext> deviceContextMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        sinkEventHolder.getSinks(DeviceOnlineStateSwitchEvent.class)
                .asFlux()
                .subscribe(
                        event -> {
                            switch (event.getOnlineState()) {
                                case ONLINE -> deviceContextMap.put(event.getDevice().getId(), DeviceContext.create(event.getDevice()));
                                case OFFLINE -> deviceContextMap.remove(event.getDevice().getId());
                            }
                        }
                );

        Flux.interval(Duration.ofSeconds(20))
                .subscribe(___ ->
                        deviceService.batchUpdateLastUpdateTime(deviceContextMap.keySet())
                );

    }


    @Nullable
    public DeviceContext getDeviceContext(Long id) {
        return deviceContextMap.get(id);
    }
}
