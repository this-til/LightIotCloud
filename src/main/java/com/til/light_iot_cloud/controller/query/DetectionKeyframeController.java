package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.data.DetectionKeyframe;
import com.til.light_iot_cloud.service.DetectionService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DetectionKeyframeController {

    @Resource
    private DetectionService detectionService;

    @SchemaMapping(typeName = "DetectionKeyframe")
    public List<Detection> detections(DetectionKeyframe detectionKeyframe) {
        LambdaQueryWrapper<Detection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Detection::getKeyframeId, detectionKeyframe.getId());

        return detectionService.list(wrapper);
    }
}
