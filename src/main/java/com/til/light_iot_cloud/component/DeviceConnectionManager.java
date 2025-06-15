package com.til.light_iot_cloud.component;

import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.enums.OnlineState;
import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DeviceConnectionManager {

    @Resource
    private SinkEventHolder sinkEventHolder;

    private final Map<String, AuthContext> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, WebSocketSessionInfo>> devices = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        Flux.interval(Duration.ofSeconds(60))
                .subscribe(
                        l -> sessions.values()
                                .stream()
                                .filter(a -> !a.getWebSocketSession().isOpen())
                                .toList()
                                .forEach(a -> unregisterSession(a.getWebSocketSession().getId()))
                );
    }

    public synchronized void registerSession(AuthContext authContext) {

        LinkType linkType = authContext.getLinkType();
        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        WebSocketSessionInfo webSocketSessionInfo = authContext.getWebSocketSessionInfo();

        String socketId = webSocketSessionInfo.getId();

        if (sessions.containsKey(socketId)) {
            return;
        }

        sessions.put(socketId, authContext);

        Long id = authContext.getDevice().getId();
        Map<String, WebSocketSessionInfo> map = devices.computeIfAbsent(id, ___ -> {
            sinkEventHolder.publishEvent(
                    new DeviceOnlineStateSwitchEvent(
                            OnlineState.ONLINE,
                            authContext.getDevice()
                    )
            );
            return new ConcurrentHashMap<>();
        });
        map.put(socketId, webSocketSessionInfo);
    }

    @SneakyThrows
    public synchronized void unregisterSession(String sessionId) {

        if (!sessions.containsKey(sessionId)) {
            return;
        }

        AuthContext authContext = sessions.remove(sessionId);

        if (authContext == null) {
            return;
        }

        Long id = authContext.getDevice().getId();

        if (devices.containsKey(id)) {
            Map<String, WebSocketSessionInfo> map = devices.get(id);
            map.remove(sessionId);
            if (map.isEmpty()) {
                devices.remove(id);
                sinkEventHolder.publishEvent(
                        new DeviceOnlineStateSwitchEvent(
                                OnlineState.OFFLINE,
                                authContext.getDevice()
                        )
                );
            }
        }

    }

    @Nullable
    public AuthContext getPublisherBySession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Nullable
    public Map<String, WebSocketSessionInfo> getPublisherByDeviceId(Long deviceId) {
        return devices.get(deviceId);
    }


}
