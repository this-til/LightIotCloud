package com.til.light_iot_cloud.data;

import com.til.light_iot_cloud.enums.LinkType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Data
@NoArgsConstructor
public class AuthContext {
    LinkType linkType;

    User user;

    /***
     * 作为灯杆登入
     * Nullable
     */
    Light light;

    /***
     * 作为检测小车登入
     * Nullable
     */
    Car car;

    /***
     * 若为WebSocket 表示对应的链接
     * Nullable
     */
    WebSocketSession webSocketSession;

    public AuthContext(LinkType linkType, User user) {
        this.linkType = linkType;
        this.user = user;
    }
}
