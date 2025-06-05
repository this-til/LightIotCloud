package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.data.Light;
import com.til.light_iot_cloud.data.input.LightStateInput;
import lombok.Data;
import org.springframework.graphql.server.WebSocketSessionInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class LightContext  {

    Long id;
    LightStateInput lightStateInput;

    public LightContext(Long id) {
        this.id = id;
    }

}
