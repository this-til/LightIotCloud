package com.til.light_iot_cloud.interceptor;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(final @NotNull HttpServletRequest request, final @NotNull HttpServletResponse response, final @NotNull Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");

        // If no Authorization header, return unauthorized
        if (authHeader == null || authHeader.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        try {
            // Validate JWT token and get user
            Long userId = jwtTokenConfig.parseJwt(authHeader);
            User user = userService.getUserById(userId);

            // Set AuthContext as request attribute
            request.setAttribute("authContext", new AuthContext(LinkType.HTTP, user));
            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
