package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
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
            throw new SecurityException("You are not logged in");
        }
        return authContext.getUser();
    }

    @QueryMapping
    public Light lightSelf(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET_LIGHT) {
            throw new SecurityException("You are not logged in");
        }
        return authContext.getLight();
    }

    @QueryMapping
    public Car carSelf(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET_CAR) {
            throw new SecurityException("You are not logged in");
        }
        return authContext.getCar();
    }

}
