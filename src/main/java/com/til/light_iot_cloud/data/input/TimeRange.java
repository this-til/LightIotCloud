package com.til.light_iot_cloud.data.input;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@AllArgsConstructor
public class TimeRange {

    OffsetDateTime start;
    OffsetDateTime end;

    public void standard() {
        if (start == null) {
            start = OffsetDateTime.MIN;
        }
        if (end == null) {
            end = OffsetDateTime.MAX;
        }
        if (start.isAfter(end)) {
            OffsetDateTime c = end;
            end = start;
            start = c;
        }
    }

}
