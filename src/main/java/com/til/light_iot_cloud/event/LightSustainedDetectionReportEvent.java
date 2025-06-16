package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.Detection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class LightSustainedDetectionReportEvent implements ISinksEvent {
    Long lightId;
    List<Detection> detections;
}
