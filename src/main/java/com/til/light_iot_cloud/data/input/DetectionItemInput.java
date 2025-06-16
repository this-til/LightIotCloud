package com.til.light_iot_cloud.data.input;

import com.til.light_iot_cloud.data.Detection;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class DetectionItemInput {
    String model;
    String item;
    Double x;
    Double y;
    Double w;
    Double h;
    Double probability;

    @NotNull
    public Detection asDetection() {
        Detection detection = new Detection();

        detection.setProbability(getProbability());
        detection.setW(getW());
        detection.setH(getH());
        detection.setX(getX());
        detection.setY(getY());
        detection.setModel(getModel());
        detection.setItem(getItem());

        return detection;
    }
}
