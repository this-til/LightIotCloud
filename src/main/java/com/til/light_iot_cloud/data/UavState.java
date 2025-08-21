package com.til.light_iot_cloud.data;

import lombok.Data;

@Data
public class UavState {
    V3 acceleratedSpeed;
    V3 angularVelocity;
    V3 angle;
    Quaternion quaternion;
    Float airPressure;
    Float elevation;
}
