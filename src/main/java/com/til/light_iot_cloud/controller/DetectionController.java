package com.til.light_iot_cloud.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.data.YoloItem;
import com.til.light_iot_cloud.data.YoloModel;
import com.til.light_iot_cloud.service.DetectionService;
import com.til.light_iot_cloud.service.YoloItemService;
import com.til.light_iot_cloud.service.YoloModelService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Objects;

@Controller
public class DetectionController {

    @Resource
    private YoloItemService yoloItemService;

    @Resource
    private YoloModelService yoloModelService;

    @SchemaMapping(typeName = "Detection")
    public String model(Detection detection) {
        YoloItem item = yoloItemService.getOne(
                new LambdaQueryWrapper<YoloItem>()
                        .eq(YoloItem::getId, detection.getItemId())
        );

        if (item == null) {
            return "null";
        }

        YoloModel model = yoloModelService.getOne(
                new LambdaQueryWrapper<YoloModel>()
                        .eq(YoloModel::getId, item.getModelId())
        );

        if (model == null) {
            return "null";
        }

        return model.getName();

    }

    @SchemaMapping(typeName = "Detection")
    public String item(Detection detection) {
        YoloItem item = yoloItemService.getOne(
                new LambdaQueryWrapper<YoloItem>()
                        .eq(YoloItem::getId, detection.getItemId())
        );

        if (item == null) {
            return "null";
        }

        return item.getName();

    }

}
