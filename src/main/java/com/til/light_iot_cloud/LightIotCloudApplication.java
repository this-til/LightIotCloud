package com.til.light_iot_cloud;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableTransactionManagement
@EnableWebSocket
@MapperScan("com.til.light_iot_cloud.mapper")
public class LightIotCloudApplication {

    static {
        ByteBuddyAgent.install();
    }

    public static void main(String[] args) {
        //GraphQLWebSocketInjector.install();
        SpringApplication.run(LightIotCloudApplication.class, args);
    }

}
