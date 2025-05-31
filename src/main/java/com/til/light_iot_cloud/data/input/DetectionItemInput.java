package com.til.light_iot_cloud.data.input;

import lombok.Data;

@Data
public class DetectionItemInput {
    String model;
    String item;
    Float x;
    Float y;
    Float w;
    Float h;
    Float probability;
}
