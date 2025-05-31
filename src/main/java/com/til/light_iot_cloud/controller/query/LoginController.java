package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class LoginController {

    @Resource
    public UserService userService;

    @QueryMapping()
    public String login(@Argument String username, @Argument String password) {
        return userService.login(username, password);
    }

    @QueryMapping()
    public boolean register(@Argument String username, @Argument String password) {
        userService.register(username, password);
        return true;
    }

}
