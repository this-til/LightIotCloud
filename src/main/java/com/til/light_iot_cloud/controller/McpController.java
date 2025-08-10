package com.til.light_iot_cloud.controller;

import com.til.light_iot_cloud.config.JwtTokenConfig;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.service.UserService;
import com.til.light_iot_cloud.data.User;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.schema.idl.SchemaPrinter;
import jakarta.annotation.Nullable;
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

/**
 * MCP (Model Context Protocol) 控制器
 * <p>
 * 本控制器为 AI 模型提供访问 GraphQL API 的能力，通过 Spring AI 的工具注解
 * 将 GraphQL 查询和变更操作暴露为 AI 工具函数。支持获取 GraphQL schema
 * 文档和执行 GraphQL 查询语句。
 * <p>
 * 主要功能：
 * - 提供 GraphQL schema 文档查询
 * - 执行 GraphQL 查询和变更操作
 * - 支持带 JWT 令牌的身份验证
 * - 为 AI 模型提供结构化的 API 访问能力
 * <p>
 * 使用场景：
 * - AI 助手通过自然语言与系统交互
 * - 自动化数据查询和操作
 * - 智能数据分析和报告生成
 * 
 * @author TIL
 */
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

    /**
     * 获取 GraphQL Schema 文档
     * <p>
     * 返回当前系统的 GraphQL schema 描述文档，包含所有可用的查询、变更和订阅操作，
     * 以及相关的数据类型定义。AI 模型可以通过此文档了解 API 的完整结构。
     * 
     * @return GraphQL schema 的字符串表示
     */
    @Tool(description = "获取 graphql 的描述文档")
    public String getSchema() {
        return this.printer.print(this.graphQlSource.schema());
    }

    /**
     * 执行 GraphQL 查询
     * <p>
     * 执行标准的 GraphQL 查询或变更操作，支持变量传递和身份验证。
     * 如果提供了有效的 JWT 令牌，查询将在相应用户的权限上下文中执行；
     * 否则将以匿名用户身份执行，只能访问公开的操作（如登录）。
     * <p>
     * 注意：建议在使用此方法前先调用 getSchema() 获取 API 文档。
     * 
     * @param query GraphQL 标准查询语句，支持 Query 和 Mutation 操作
     * @param variables GraphQL 查询变量，可选参数，用于参数化查询
     * @param token JWT 身份认证令牌，可选参数。如未提供则以匿名身份执行
     * @return GraphQL 执行结果，包含数据和可能的错误信息
     */
    @Tool(description = "执行graphql查询语句并且返回结果，如果你想也可以执行Mutation。注：无论如何请先调用getSchema以获取graphql的描述文档")
    public ExecutionResult query(
            @ToolParam(description = "graphql标准的查询语句") String query,
            @Nullable @ToolParam(description = "graphql标准变量", required = false) Map<String, Object> variables,
            @Nullable @ToolParam(description = "JWT令牌，可以接受无令牌操作但只能访问Mutation.login，若没有则使用Mutation.login获取", required = false) String token
    ) {

        ExecutionInput.Builder builder = ExecutionInput.newExecutionInput().query(query);

        if (variables != null) {
            builder.variables(variables);
        }

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
