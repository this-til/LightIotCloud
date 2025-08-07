package com.til.light_iot_cloud.event;

import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.AlarmDialogueOperateType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlarmDialogueOperateEvent implements ISinksEvent {
    Device light;
    AlarmDialogueOperateType alarmDialogueOperateType;
}
