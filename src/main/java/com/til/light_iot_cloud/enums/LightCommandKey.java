package com.til.light_iot_cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum LightCommandKey {
    CAMERA_PTZ_CONTROL("Camera.PtzControl"),

    DEVICE_GEAR("Device.Gear"),
    DEVICE_SWITCH("Device.Switch"),
    DEVICE_ROLLING_DOOR("Device.RollingDoor"),

    DETECTION_SUSTAINED("Detection.Sustained"),

    BROADCAST_FILE("Broadcast.File"),
    BROADCAST_STOP("Broadcast.Stop"),

    UAV_BASE_STATION_COVER("UavBaseStation.Cover"),
    UAV_BASE_STATION_CLAMP("UavBaseStation.Clamp"),
    ;


    private final String value;
}