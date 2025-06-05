package com.til.light_iot_cloud.data.input;

import lombok.Data;

@Data
public class OperationCarInput {
    Boolean translationAdvance;
    Boolean translationLeft;
    Boolean translationRetreat;
    Boolean translationRight;
    Boolean angularLeft;
    Boolean angularRight;
    Boolean stop;
}
