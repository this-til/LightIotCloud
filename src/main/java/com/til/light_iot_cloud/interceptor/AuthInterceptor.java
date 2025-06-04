package com.til.light_iot_cloud.interceptor;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.data.User;
import com.til.light_iot_cloud.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class AuthInterceptor implements WebGraphQlInterceptor {

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Resource
    private UserService userService;

    @Override
    public @NotNull Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, @NotNull Chain chain) {

        if (request.getAttributes().get(ConnectionInterceptor.AUTH_CONTEXT) instanceof AuthContext authContext) {
            request.configureExecutionInput(
                    (executionInput, builder) ->
                            builder.graphQLContext(
                                    contextBuilder -> contextBuilder.put("authContext", authContext)
                            ).build()
            );
            return chain.next(request);
        }

        @Nullable String authHeader = request.getHeaders().getFirst("Authorization");
        request.configureExecutionInput(
                (executionInput, builder) ->
                        builder.graphQLContext(
                                contextBuilder -> {
                                    User user = null;
                                    if (authHeader != null) {
                                        user = userService.getUserById(jwtTokenConfig.parseJwt(authHeader));
                                    }
                                    contextBuilder.put("authContext", new AuthContext(LinkType.HTTP, user));
                                }
                        ).build()
        );
        return chain.next(request);
    }

}