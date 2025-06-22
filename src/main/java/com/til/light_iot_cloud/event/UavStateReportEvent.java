package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.UavState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UavStateReportEvent implements ISinksEvent {

    Device device;
    UavState uavState;

}
