package com.til.light_iot_cloud.data;

import com.til.light_iot_cloud.enums.DeviceType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 请求统计数据类
 */
@Data
public class RequestStatistics {
    Long totalHttpRequests;
    Long totalHttpErrors;
}
