package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.DetectionItem;
import com.til.light_iot_cloud.data.DetectionModel;
import com.til.light_iot_cloud.service.DetectionItemService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DetectionModelController {
    @Resource
    private DetectionItemService detectionItemService;

    @SchemaMapping(typeName = "DetectionModel")
    public List<DetectionItem> items(DetectionModel detectionModel) {
        return detectionItemService.list(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getModelId, detectionModel.getId())
        );
    }
}
