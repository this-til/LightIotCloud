package com.til.light_iot_cloud;

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

    public static void main(String[] args) {
        SpringApplication.run(LightIotCloudApplication.class, args);
    }

}
