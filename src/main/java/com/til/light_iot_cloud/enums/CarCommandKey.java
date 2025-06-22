package com.til.light_iot_cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum CarCommandKey {
    OPERATION("Operation"),

    BROADCAST_FILE("Broadcast.File"),
    BROADCAST_STOP("Broadcast.Stop");

    private final String value;

}
