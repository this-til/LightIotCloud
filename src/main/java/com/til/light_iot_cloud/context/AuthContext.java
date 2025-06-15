package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.web.socket.WebSocketSession;

@Data
@NoArgsConstructor
public class AuthContext {
    LinkType linkType;

    User user;

    /***
     * 作为设备
     * Nullable
     */
    Device device;


    /***
     * 若为WebSocket 表示对应的链接
     * Nullable
     */
    WebSocketSession webSocketSession;
    WebSocketSessionInfo webSocketSessionInfo;

    public AuthContext(LinkType linkType, User user) {
        this.linkType = linkType;
        this.user = user;
    }

}
