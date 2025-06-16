package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.Device;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommandDownEvent implements  ISinksEvent {
    Long deviceId;

    String key;
    String value;
}
