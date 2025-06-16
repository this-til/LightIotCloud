package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class QueryController {

    @Resource
    public UserService userService;

    @SuppressWarnings("SpringGraphQLUnmatchedMappingInspection")
    @Deprecated
    @QueryMapping
    public User temporary(@Argument String username, @Argument String password) {
        return userService.temporary(username, password);
    }

    @QueryMapping
    public User self(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            return null;
        }
        return authContext.getUser();
    }

    @QueryMapping
    public Device deviceSelf(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.DEVICE_WEBSOCKET) {
            return null;
        }
        return authContext.getDevice();
    }

}
