package com.til.light_iot_cloud.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatPack {
    Integer width;
    Integer height;
    Integer channels;

    byte[] image;
}
