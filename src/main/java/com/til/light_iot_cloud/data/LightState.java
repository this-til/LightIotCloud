package com.til.light_iot_cloud.data;

import lombok.Data;

@Data
public class LightState {
    Float electricity;
    Float voltage;
    Float power;

    Float wirelessChargingElectricity;
    Float wirelessChargingVoltage;
    Float wirelessChargingPower;
}
