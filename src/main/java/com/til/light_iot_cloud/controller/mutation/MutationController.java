package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.service.DeviceService;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;

@Controller
public class MutationController {

    @Resource
    private UserService userService;

    @Resource
    private DeviceService deviceService;

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @MutationMapping()
    public String login(@Argument String username, @Argument String password) {
        return userService.login(username, password);
    }

    @MutationMapping()
    public boolean register(@Argument String username, @Argument String password) {
        userService.register(username, password);
        return true;
    }

    @MutationMapping
    public boolean jwtEffective(@Argument String jwt) {
        try {
            jwtTokenConfig.parseJwt(jwt);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @MutationMapping
    public User self(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            throw new SecurityException("You are not logged in");
        }
        return authContext.getUser();
    }

    @MutationMapping
    public Device deviceSelf(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            throw new SecurityException("You are not logged in");
        }
        if (authContext.getDevice() == null) {
            throw new SecurityException("The logged is not device");
        }
        return authContext.getDevice();
    }


}
