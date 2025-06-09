package com.til.light_iot_cloud.data.input;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.til.light_iot_cloud.data.DetectionKeyframe;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public class DetectionKeyframesFilter {
    @Nullable
    Page<DetectionKeyframe> page;

    @Nullable
    TimeRange timeRange;

    @Nullable
    List<ModelSelect> models;
}
