package com.til.light_iot_cloud.controller;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.service.UserService;
import com.til.light_iot_cloud.data.User;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.schema.idl.SchemaPrinter;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.execution.GraphQlSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Log4j2
@Controller
public class McpController {
    @Resource
    private GraphQlSource graphQlSource;

    @Resource
    private JwtTokenConfig jwtTokenConfig;

    @Resource
    private UserService userService;

    private final SchemaPrinter printer = new SchemaPrinter();

    @Tool(description = "获取 graphql 的描述文档")
    public String getSchema() {
        return this.printer.print(this.graphQlSource.schema());
    }

    @Tool(description = "执行graphql查询语句并且返回结果，如果你想也可以执行Mutation。注：需要在请求头中包含Authorization JWT令牌")
    public ExecutionResult query(
            @ToolParam(description = "graphql标准的查询语句") String query,
            @ToolParam(description = "graphql标准变量") Map<String, Object> variables,

            @RequestHeader("Authorization") @ToolParam(required = false) String token
    ) {

        ExecutionInput.Builder builder = ExecutionInput.newExecutionInput()
                .query(query)
                .variables(variables);

        AuthContext authContext = null;
        if (token != null) {
            User user = null;
            try {
                Long userId = jwtTokenConfig.parseJwt(token);
                user = userService.getUserById(userId);
            } catch (Exception e) {
                log.warn("解析令牌错误，将使用无用户操作...");
            }

            // 构建AuthContext并设置到GraphQL上下文中
            authContext = new AuthContext(LinkType.HTTP, user);

        }

        if (authContext != null) {
            AuthContext finalAuthContext = authContext;
            builder.graphQLContext(contextBuilder ->
                    contextBuilder.put("authContext", finalAuthContext)
            );
        }

        return graphQlSource.graphQl().execute(builder.build());
    }
}
