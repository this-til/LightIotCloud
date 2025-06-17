package com.til.light_iot_cloud.data;

import lombok.Data;

@Data
public class LightState {
    Float wirelessChargingElectricity;
    Float wirelessChargingVoltage;
    Float wirelessChargingPower;
}
