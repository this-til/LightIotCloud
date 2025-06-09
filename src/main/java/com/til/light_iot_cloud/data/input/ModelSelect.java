package com.til.light_iot_cloud.data.input;

import lombok.Data;

import java.util.List;

@Data
public class ModelSelect {
    Long id;
    List<Long> itemIds;
}
