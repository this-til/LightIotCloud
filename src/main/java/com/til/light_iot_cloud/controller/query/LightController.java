package com.til.light_iot_cloud.controller.query;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.til.light_iot_cloud.component.WebSocketConnectionManager;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.TimeRange;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.*;

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

    @Resource
    private WebSocketConnectionManager webSocketConnectionManager;


    @SchemaMapping(typeName = "Light")
    public boolean online(Light light) {
        return webSocketConnectionManager.getPublisherByLightId(light.getId()) != null;
    }

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
        return lightDataService.list(new Page<>(0, 5000), listQuery);
    }

    @SchemaMapping(typeName = "Light")
    public List<DetectionKeyframe> detectionKeyframes(
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

        return detectionKeyframeService.list(page, listQuery);
    }
}

