package com.til.light_iot_cloud.data.input;

import lombok.Data;

@Data
public class LightStateInput {
    Boolean enableWirelessCharging;
    Float wirelessChargingPower;
}
