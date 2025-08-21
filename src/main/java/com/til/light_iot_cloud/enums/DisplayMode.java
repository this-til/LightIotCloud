package com.til.light_iot_cloud.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum DisplayMode {
    ENVIRONMENT("environment"),
    CAROUSEL("carousel");


    private final String value;
}
