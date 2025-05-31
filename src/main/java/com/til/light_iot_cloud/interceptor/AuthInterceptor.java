package com.til.light_iot_cloud.interceptor;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.data.AuthContext;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

public class AuthInterceptor implements WebGraphQlInterceptor {

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Resource
    private UserService userService;

    @Override
    public @NotNull Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, @NotNull Chain chain) {
        @Nullable String authHeader = request.getHeaders().getFirst("Authorization");
        request.configureExecutionInput((executionInput, builder) ->
                builder.graphQLContext(contextBuilder -> {
                            User userById = null;
                            if (authHeader != null) {
                                userById = userService.getUserById(jwtTokenConfig.parseJwt(authHeader));
                            }
                            contextBuilder.put("authContext", new AuthContext(authHeader, userById));
                        }
                ).build()
        );

        return chain.next(request);
    }
}