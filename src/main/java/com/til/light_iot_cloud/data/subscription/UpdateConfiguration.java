package com.til.light_iot_cloud.data.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateConfiguration {
    String key;
    String value;
}
