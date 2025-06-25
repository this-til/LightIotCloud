package com.til.light_iot_cloud.data;

import com.til.light_iot_cloud.enums.RollingDoorState;
import lombok.Data;

@Data
public class LightState {
    PowerPack selfPower;
    PowerPack wirelessChargingPower;
    PowerPack uavPower;
    PowerPack uavBaseStationPower;

    Boolean automaticGear;
    Float gear;
    RollingDoorState rollingDoorState;
}
