package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.data.Light;
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

    DeviceType deviceType;

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
    WebSocketSessionInfo webSocketSessionInfo;

    public AuthContext(LinkType linkType, User user) {
        this.linkType = linkType;
        this.user = user;
    }

    public Long getDeviceId() {
        if (deviceType == DeviceType.LIGHT) {
            return light.getId();
        }
        if (deviceType == DeviceType.CAR) {
            return car.getId();
        }
        return -1L;
    }
}
