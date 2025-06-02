package com.til.light_iot_cloud.data;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthContext {
    LinkType linkType;


    @Nullable
    User user;

    /***
     * 作为灯杆登入
     */
    @Nullable
    Light light;

    /***
     * 作为检测小车登入
     */
    @Nullable
    Car car;

    public AuthContext(LinkType linkType, @Nullable User user) {
        this.linkType = linkType;
        this.user = user;
    }
}
