package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.controller.query.CarController;
import lombok.Data;
import org.springframework.graphql.server.WebSocketSessionInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CarContext {
    Long id;


    public CarContext(Long id) {
        this.id = id;
    }
}
