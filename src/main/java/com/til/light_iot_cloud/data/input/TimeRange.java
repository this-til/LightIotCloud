package com.til.light_iot_cloud.data.input;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@AllArgsConstructor
public class TimeRange {
    // 极小值（表示时间轴上最早的时刻）
    public static final OffsetDateTime MIN_TIME = OffsetDateTime.ofInstant(Instant.MIN, ZoneOffset.UTC);
    // 极大值（表示时间轴上最晚的时刻）
    public static final OffsetDateTime MAX_TIME = OffsetDateTime.ofInstant(Instant.MAX, ZoneOffset.UTC);

    OffsetDateTime start;
    OffsetDateTime end;

    public void standard() {
        if (start == null) {
            start = MIN_TIME;
        }
        if (end == null) {
            end = MAX_TIME;
        }
        if (start.isAfter(end)) {
            OffsetDateTime c = end;
            end = start;
            start = c;
        }
    }

}
