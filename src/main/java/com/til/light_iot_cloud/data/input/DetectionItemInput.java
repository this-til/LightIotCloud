package com.til.light_iot_cloud.data.input;

import lombok.Data;

@Data
public class DetectionItemInput {
    String model;
    String item;
    Double x;
    Double y;
    Double w;
    Double h;
    Double probability;
}
