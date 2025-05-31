package com.til.light_iot_cloud.data;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthContext {

    String token;
    User user;
}
