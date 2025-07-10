package com.til.light_iot_cloud.config;

import com.til.light_iot_cloud.controller.McpController;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpServerConfig {

    @Bean
    public ToolCallbackProvider bookToolCallbackProvider(McpController mcpController) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(mcpController)
                .build();
    }

}
