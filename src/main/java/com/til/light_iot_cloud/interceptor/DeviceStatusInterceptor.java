package com.til.light_iot_cloud.interceptor;

import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.service.CarService;
import com.til.light_iot_cloud.service.LightService;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Component
public class DeviceStatusInterceptor implements WebSocketGraphQlInterceptor {

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DEVICE_NAME = "deviceName";
    public static final String LINK_TYPE = LinkType.KEY;
    public static final String AUTH_CONTEXT = "authContext";

    @Resource
    private CarService carService;

    @Resource
    private LightService lightService;

    @Resource
    private UserService userService;

    @Override
    public @NotNull
    Mono<Object> handleConnectionInitialization(@NotNull WebSocketSessionInfo info, Map<String, Object> payload) {

        String username = Objects.requireNonNullElse(payload.get(USERNAME), "").toString();
        String password = Objects.requireNonNullElse(payload.get(PASSWORD), "").toString();

        if (username.isEmpty() || password.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Username and password cannot be empty"));
        }

        User user = userService.temporary(username, password);

        if (user == null) {
            return Mono.error(new IllegalArgumentException("Username or password is incorrect"));
        }

        if (!payload.containsKey(LINK_TYPE)) {
            info.getAttributes().put(LINK_TYPE, LinkType.WEBSOCKET);
            AuthContext authContext = new AuthContext(LinkType.WEBSOCKET, user);
            info.getAttributes().put(AUTH_CONTEXT, authContext);
            return Mono.just(payload);
        }

        String linkTypeStr = Objects.requireNonNullElse(payload.get(LINK_TYPE), "").toString();

        if (linkTypeStr.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Link type cannot be empty"));
        }

        LinkType linkType;
        try {
            linkType = LinkType.valueOf(linkTypeStr);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid link type: " + linkTypeStr));
        }

        if (linkType == LinkType.HTTP) {
            return Mono.error(new IllegalArgumentException("Only web socket links are supported"));
        }

        if (linkType == LinkType.WEBSOCKET) {
            info.getAttributes().put(LINK_TYPE, LinkType.WEBSOCKET);
            AuthContext authContext = new AuthContext(LinkType.WEBSOCKET, user);
            info.getAttributes().put(AUTH_CONTEXT, authContext);
            return Mono.just(payload);
        }

        if (!info.getAttributes().containsKey(DEVICE_NAME)) {
            return Mono.error(new IllegalArgumentException("Device name is missing"));
        }

        String deviceName = Objects.requireNonNullElse(payload.get(DEVICE_NAME), "").toString();

        if (deviceName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Device name is missing"));
        }

        switch (linkType) {
            case WEBSOCKET_LIGHT -> {
                Light light = lightService.getLightByName(user.getId(), deviceName);

                if (light == null) {
                    return Mono.error(new IllegalArgumentException("Device " + deviceName + " not found"));
                }

                AuthContext authContext = new AuthContext(LinkType.WEBSOCKET_LIGHT, user);
                authContext.setLight(light);

                info.getAttributes().put(LINK_TYPE, LinkType.WEBSOCKET_LIGHT);
                info.getAttributes().put(AUTH_CONTEXT, authContext);
                return Mono.just(payload);
            }
            case WEBSOCKET_CAR -> {

                Car car = carService.getCarByName(user.getId(), deviceName);

                if (car == null) {
                    return Mono.error(new IllegalArgumentException("Device " + deviceName + " not found"));
                }

                AuthContext authContext = new AuthContext(LinkType.WEBSOCKET_CAR, user);
                authContext.setCar(car);
                info.getAttributes().put(LINK_TYPE, LinkType.WEBSOCKET_CAR);
                info.getAttributes().put(AUTH_CONTEXT, authContext);
                return Mono.just(payload);
            }
            default -> {
                return Mono.error(new IllegalArgumentException("Invalid link type: " + linkTypeStr));
            }
        }

    }

    @Override
    public void handleConnectionClosed(@NotNull WebSocketSessionInfo sessionInfo, int statusCode, @NotNull Map<String, Object> connectionInitPayload) {
    }
}
