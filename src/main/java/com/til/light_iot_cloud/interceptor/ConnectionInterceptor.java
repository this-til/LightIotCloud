package com.til.light_iot_cloud.interceptor;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.service.DeviceService;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.server.WebSocketGraphQlInterceptor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

@Component
public class ConnectionInterceptor implements WebSocketGraphQlInterceptor {

    public static final String AUTHORIZATION = "Authorization";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String DEVICE_NAME = "deviceName";
    public static final String LINK_TYPE = LinkType.KEY;
    public static final String DEVICE_TYPE = DeviceType.KEY;
    public static final String AUTH_CONTEXT = "authContext";

    @Resource
    private UserService userService;

    @Resource
    private DeviceConnectionManager deviceConnectionManager;

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Resource
    private DeviceService deviceService;


    @SneakyThrows
    @Override
    public @NotNull Mono<Object> handleConnectionInitialization(@NotNull WebSocketSessionInfo info, Map<String, Object> payload) {
        String authorization = Objects.requireNonNullElse(payload.get(AUTHORIZATION), "").toString();

        String username = Objects.requireNonNullElse(payload.get(USERNAME), "").toString();
        String password = Objects.requireNonNullElse(payload.get(PASSWORD), "").toString();
        User user;

        if (!username.isEmpty() && !password.isEmpty()) {
            user = userService.temporary(username, password);
        } else if (!authorization.isEmpty()) {

            Long userId;

            try {
                userId = jwtTokenConfig.parseJwt(authorization);
            } catch (Exception e) {
                return Mono.error(e);
            }

            user = userService.getUserById(userId);

        } else {
            return Mono.error(new IllegalArgumentException("identity verification failed"));
        }

        if (user == null) {
            return Mono.error(new IllegalArgumentException("user verification incorrect"));
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
            AuthContext authContext = new AuthContext(LinkType.WEBSOCKET, user);
            info.getAttributes().put(AUTH_CONTEXT, authContext);
            return Mono.just(payload);
        }

        String deviceTypeStr = Objects.requireNonNullElse(payload.get(DEVICE_TYPE), "").toString();

        if (deviceTypeStr.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Device type cannot be empty"));
        }

        DeviceType deviceType;

        try {
            deviceType = DeviceType.valueOf(deviceTypeStr);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Invalid device type: " + deviceTypeStr));
        }

        String deviceName = Objects.requireNonNullElse(payload.get(DEVICE_NAME), "").toString();

        if (deviceName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Device name is missing"));
        }

        Field sessionField = info.getClass().getDeclaredField("session");
        sessionField.setAccessible(true);
        WebSocketSession session = (WebSocketSession) sessionField.get(info);


        AuthContext authContext = new AuthContext(LinkType.DEVICE_WEBSOCKET, user);
        authContext.setWebSocketSession(session);
        authContext.setWebSocketSessionInfo(info);

        Device device = deviceService.existDevice(user.getId(), deviceName, deviceType);
        authContext.setDevice(device);

        deviceConnectionManager.registerSession(authContext);
        info.getAttributes().put(AUTH_CONTEXT, authContext);
        return Mono.just(payload);
    }

    @Override
    public void handleConnectionClosed(@NotNull WebSocketSessionInfo sessionInfo, int statusCode, @NotNull Map<String, Object> connectionInitPayload) {
        deviceConnectionManager.unregisterSession(sessionInfo.getId());
    }
}
