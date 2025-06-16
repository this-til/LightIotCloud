package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.data.DetectionItem;
import com.til.light_iot_cloud.data.DetectionModel;
import com.til.light_iot_cloud.service.DetectionItemService;
import com.til.light_iot_cloud.service.DetectionModelService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DetectionController {

    @Resource
    private DetectionItemService detectionItemService;

    @Resource
    private DetectionModelService detectionModelService;

    @SchemaMapping(typeName = "Detection")
    public String model(Detection detection) {
        if (detection.getModel() != null) {
            return detection.getModel();
        }
        DetectionItem item = detectionItemService.getOne(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getId, detection.getItemId())
        );

        if (item == null) {
            return null;
        }

        DetectionModel model = detectionModelService.getOne(
                new LambdaQueryWrapper<DetectionModel>()
                        .eq(DetectionModel::getId, item.getModelId())
        );

        if (model == null) {
            return null;
        }

        return model.getName();

    }

    @SchemaMapping(typeName = "Detection")
    public String item(Detection detection) {

        if (detection.getItem() != null) {
            return detection.getItem();
        }

        DetectionItem item = detectionItemService.getOne(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getId, detection.getItemId())
        );

        if (item == null) {
            return null;
        }

        return item.getName();

    }

}
