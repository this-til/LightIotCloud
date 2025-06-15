package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.DetectionKeyframe;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightDetectionReportEvent implements ISinksEvent {
    Long lightId;
    DetectionKeyframe detectionKeyframe;
}
