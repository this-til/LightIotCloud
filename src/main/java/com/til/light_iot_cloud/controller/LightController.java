package com.til.light_iot_cloud.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.incrementer.FirebirdKeyGenerator;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.DetectionInput;
import com.til.light_iot_cloud.data.input.DetectionItemInput;
import com.til.light_iot_cloud.data.input.TimeRange;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class LightController {

    @Resource
    private LightDataService lightDataService;

    @Resource
    private DetectionKeyframeService detectionKeyframeService;

    @Resource
    private DetectionItemService detectionItemService;

    @Resource
    private DetectionModelService detectionModelService;

    @Resource
    private DetectionService detectionService;

    @SchemaMapping(typeName = "Light")
    public List<LightData> datas(Light light, @Argument @Nullable TimeRange timeRange) {

        LambdaQueryWrapper<LightData> listQuery = new LambdaQueryWrapper<>();
        listQuery.eq(LightData::getLightId, light.getId());

        if (timeRange != null) {
            timeRange.standard();
            listQuery.between(
                    LightData::getTime,
                    timeRange.getStart(),
                    timeRange.getEnd()
            );
        }

        listQuery.orderByAsc(LightData::getTime);
        return lightDataService.list(listQuery);
    }

    @SchemaMapping(typeName = "Light")
    public IPage<DetectionKeyframe> detectionKeyframes(
            Light light,
            @Argument @Nullable Page<DetectionKeyframe> page,
            @Argument @Nullable TimeRange timeRange
    ) {

        LambdaQueryWrapper<DetectionKeyframe> listQuery = new LambdaQueryWrapper<>();
        listQuery.eq(DetectionKeyframe::getLightId, light.getId());

        if (timeRange != null) {
            timeRange.standard();
            listQuery.between(
                    DetectionKeyframe::getTime,
                    timeRange.getStart(),
                    timeRange.getEnd()
            );
        }

        listQuery.orderByAsc(DetectionKeyframe::getTime);

        if (page == null) {
            page = new Page<>();
        }

        return detectionKeyframeService.page(page, listQuery);
    }


    @SchemaMapping(typeName = "Light")
    public Result<Void> reportUpdata(Light light, @Argument LightData lightDataInput) {
        lightDataInput.setLightId(light.getId());
        return Result.ofBool(lightDataService.save(lightDataInput));
    }

    @SchemaMapping(typeName = "Light")
    @Transactional
    public Result<Void> reportDetection(Light light, @Argument DetectionInput detectionInput) {

        DetectionKeyframe detectionKeyframe = new DetectionKeyframe();

        detectionKeyframe.setLightId(light.getId());

        detectionKeyframeService.save(detectionKeyframe);

        List<DetectionItemInput> items = detectionInput.getItems();

        Map<String, List<DetectionItemInput>> modelMap = items.stream()
                .collect(Collectors.groupingBy(DetectionItemInput::getModel));

        Map<String, DetectionModel> stringDetectionModelMap = detectionModelService.ensureExistence(modelMap.keySet(), light.getUserId());

        List<Detection> detectionList = new ArrayList<>();

        for(Map.Entry<String, List<DetectionItemInput>> entry : modelMap.entrySet()) {

            DetectionModel detectionModel = stringDetectionModelMap.get(entry.getKey());

            List<DetectionItemInput> value = entry.getValue();

            Map<String, List<DetectionItemInput>> itemMap = value.stream()
                    .collect(Collectors.groupingBy(DetectionItemInput::getItem));

            Map<String, DetectionItem> stringDetectionItemMap = detectionItemService.ensureExistence(itemMap.keySet(), detectionModel.getId());

            for(Map.Entry<String, List<DetectionItemInput>> itemEntry : itemMap.entrySet()) {

                DetectionItem detectionItem = stringDetectionItemMap.get(itemEntry.getKey());

                for(DetectionItemInput detectionItemInput : itemEntry.getValue()) {

                    Detection detection = new Detection();

                    detection.setProbability(detectionItemInput.getProbability());
                    detection.setW(detectionItemInput.getW());
                    detection.setH(detectionItemInput.getH());
                    detection.setX(detectionItemInput.getX());
                    detection.setY(detectionItemInput.getY());

                    detection.setItemId(detectionItem.getId());
                    detection.setKeyframeId(detectionKeyframe.getId());

                    detectionList.add(detection);

                }

            }

        }

        detectionService.saveBatch(detectionList);

        return Result.ofBool(true);
    }
}

