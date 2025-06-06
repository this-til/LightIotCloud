package com.til.light_iot_cloud.data;

import lombok.Data;

@Data
public class LightState {
    Boolean enableWirelessCharging;
    Float wirelessChargingPower;
}
