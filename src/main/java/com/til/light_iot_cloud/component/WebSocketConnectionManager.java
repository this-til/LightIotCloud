package com.til.light_iot_cloud.component;

import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.context.Publisher;
import com.til.light_iot_cloud.context.AuthContext;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionManager {

    private final Map<String, Publisher> sessions = new ConcurrentHashMap<>();

    private final Map<Long, DeviceContext> lightSessions = new ConcurrentHashMap<>();
    private final Map<Long, DeviceContext> carSessions = new ConcurrentHashMap<>();

    public void registerSession(AuthContext authContext) {
        WebSocketSession webSocketSession = authContext.getWebSocketSession();

        String socketId = webSocketSession.getId();

        if (sessions.containsKey(socketId)) {
            return;
        }

        Publisher publisher = new Publisher(authContext);

        sessions.put(socketId, publisher);

        switch (authContext.getLinkType()) {
            case WEBSOCKET_LIGHT -> {
                Long id = authContext.getLight().getId();
                DeviceContext deviceContext = lightSessions.computeIfAbsent(id, _ -> new DeviceContext());
                deviceContext.register(publisher);
            }
            case WEBSOCKET_CAR -> {
                Long id = authContext.getCar().getId();
                DeviceContext deviceContext = carSessions.computeIfAbsent(id, _ -> new DeviceContext());
                deviceContext.register(publisher);
            }
        }

    }

    @SneakyThrows
    public void unregisterSession(String sessionId) {

        if (!sessions.containsKey(sessionId)) {
            return;
        }

        Publisher publisher = sessions.remove(sessionId);

        if (publisher == null) {
            return;
        }

        AuthContext authContext = publisher.getAuthContext();

        switch (authContext.getLinkType()) {
            case WEBSOCKET_LIGHT -> {
                Long id = authContext.getLight().getId();
                if (lightSessions.containsKey(id)) {
                    DeviceContext deviceContext = lightSessions.get(id);
                    deviceContext.unregister(publisher);
                    if (deviceContext.isEmpty()) {
                        lightSessions.remove(id);
                    }
                }
            }
            case WEBSOCKET_CAR -> {
                Long id = authContext.getCar().getId();
                if (carSessions.containsKey(id)) {
                    carSessions.get(id).unregister(publisher);
                    if (carSessions.get(id).isEmpty()) {
                        carSessions.remove(id);
                    }
                }
            }
        }

        if (!publisher.isReleased()) {
            publisher.release();
        }
    }

    @Nullable
    public Publisher getPublisherBySession(String sessionId) {
        return sessions.get(sessionId);
    }

    @Nullable
    public DeviceContext getPublisherByLightId(Long lightId) {
        return lightSessions.get(lightId);
    }

    @Nullable
    public DeviceContext getPublisherByCarId(Long carId) {
        return carSessions.get(carId);
    }


}
