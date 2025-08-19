package com.til.light_iot_cloud.handler;

import com.til.light_iot_cloud.component.RequestStatisticsManager;
import com.til.light_iot_cloud.config.GraphQLConfig;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.webmvc.GraphQlWebSocketHandler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
public class ExtendGraphQlWebSocketHandler extends GraphQlWebSocketHandler {

    @Value("${extendGraphQlWebSocketHandler.pingTimeout}")
    private Duration pingTimeout;

    @Value("${extendGraphQlWebSocketHandler.checkInterval}")
    private Duration checkInterval;

    @Resource
    private RequestStatisticsManager requestStatisticsManager;

    private long pingTimeoutMs;

    private final Map<String, SessionStateExtension> stringSessionStateExtensionMap;

    public ExtendGraphQlWebSocketHandler(GraphQLConfig graphQLConfig, WebGraphQlHandler webGraphQlHandler, HttpMessageConverters converters, GraphQlProperties properties) {
        super(webGraphQlHandler, graphQLConfig.getJsonConverter(converters), properties.getWebsocket().getConnectionInitTimeout(), properties.getWebsocket().getKeepAlive());
        stringSessionStateExtensionMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        Mono.delay(checkInterval)
                .repeat()
                .subscribe(__ -> checkSessionsTimeout());
        pingTimeoutMs = pingTimeout.toMillis();
    }

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage webSocketMessage) throws Exception {
        super.handleTextMessage(session, webSocketMessage);

        SessionStateExtension state = stringSessionStateExtensionMap.computeIfAbsent(
                session.getId(),
                id -> new SessionStateExtension(session)
        );

        state.updateLastPingTime();
        requestStatisticsManager.addRequests();
    }

    private void checkSessionsTimeout() {
        long currentTime = System.currentTimeMillis();
        if (!stringSessionStateExtensionMap.isEmpty()) {
            stringSessionStateExtensionMap.entrySet().removeIf(
                    entry -> {
                        SessionStateExtension state = entry.getValue();
                        WebSocketSession session = state.getSession();
                        if (!session.isOpen()) {
                            return true;
                        }
                        if (currentTime - state.getLastPingTime() > pingTimeoutMs) {
                            try {
                                session.close(CloseStatus.SESSION_NOT_RELIABLE);
                            } catch (IOException e) {
                                log.error("关闭会话失败: {}", e.getMessage());
                            }
                            return true;
                        }
                        return false;
                    }
            );
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus closeStatus) {
        super.afterConnectionClosed(session, closeStatus);
        stringSessionStateExtensionMap.remove(session.getId());
    }

    static class SessionStateExtension {
        private final AtomicLong lastPingTime = new AtomicLong(System.currentTimeMillis());
        @Getter
        @Setter
        private WebSocketSession session;

        public SessionStateExtension(WebSocketSession session) {
            this.session = session;
        }

        public void updateLastPingTime() {
            lastPingTime.set(System.currentTimeMillis());
        }

        public long getLastPingTime() {
            return lastPingTime.get();
        }

    }
}
