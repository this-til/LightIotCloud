package com.til.light_iot_cloud.component;

import com.til.light_iot_cloud.context.AuthContext;
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

    private final Map<Long, Map<String, WebSocketSessionInfo>> lightSessions = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, WebSocketSessionInfo>> carSessions = new ConcurrentHashMap<>();

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

        switch (authContext.getDeviceType()) {
            case LIGHT -> {
                Long id = authContext.getLight().getId();
                Map<String, WebSocketSessionInfo> map = lightSessions.computeIfAbsent(id, ___ -> {
                    sinkEventHolder.publishEvent(
                            new DeviceOnlineStateSwitchEvent(
                                    OnlineState.ONLINE,
                                    DeviceType.LIGHT,
                                    id
                            )
                    );
                    return new ConcurrentHashMap<>();
                });
                map.put(socketId, webSocketSessionInfo);
            }
            case CAR -> {
                Long id = authContext.getCar().getId();
                Map<String, WebSocketSessionInfo> map = carSessions.computeIfAbsent(id, ___ -> {
                    sinkEventHolder.publishEvent(
                            new DeviceOnlineStateSwitchEvent(
                                    OnlineState.ONLINE,
                                    DeviceType.CAR,
                                    id
                            )
                    );
                    return new ConcurrentHashMap<>();
                });
                map.put(socketId, webSocketSessionInfo);
            }
        }

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

        switch (authContext.getDeviceType()) {
            case LIGHT -> {
                Long id = authContext.getLight().getId();
                if (lightSessions.containsKey(id)) {
                    Map<String, WebSocketSessionInfo> map = lightSessions.get(id);
                    map.remove(sessionId);
                    if (map.isEmpty()) {
                        lightSessions.remove(id);
                        sinkEventHolder.publishEvent(
                                new DeviceOnlineStateSwitchEvent(
                                        OnlineState.OFFLINE,
                                        DeviceType.LIGHT,
                                        id
                                )
                        );
                    }
                }
            }
            case CAR -> {
                Long id = authContext.getCar().getId();
                if (carSessions.containsKey(id)) {
                    Map<String, WebSocketSessionInfo> map = carSessions.get(id);
                    map.remove(sessionId);
                    if (map.isEmpty()) {
                        carSessions.remove(id);
                        sinkEventHolder.publishEvent(
                                new DeviceOnlineStateSwitchEvent(
                                        OnlineState.OFFLINE,
                                        DeviceType.CAR,
                                        id
                                )
                        );
                    }
                }
            }
        }
    }

    @Nullable
    public AuthContext getPublisherBySession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Nullable
    public Map<String, WebSocketSessionInfo> getPublisherByLightId(Long lightId) {
        return lightSessions.get(lightId);
    }

    @Nullable
    public Map<String, WebSocketSessionInfo> getPublisherByCarId(Long carId) {
        return carSessions.get(carId);
    }

}
