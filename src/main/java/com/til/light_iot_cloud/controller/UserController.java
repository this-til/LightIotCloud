package com.til.light_iot_cloud.controller;

import com.til.light_iot_cloud.data.AuthContext;
import com.til.light_iot_cloud.data.User;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

    @QueryMapping
    public User self(@ContextValue AuthContext authContext) {
        if (authContext.getUser() == null) {
            throw new SecurityException("You are not logged in");
        }
        return authContext.getUser();
    }
}
