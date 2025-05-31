package com.til.light_iot_cloud.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.DetectionKeyframe;
import com.til.light_iot_cloud.data.Light;
import com.til.light_iot_cloud.data.LightData;
import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.data.input.TimeRange;
import com.til.light_iot_cloud.service.DetectionKeyframeService;
import com.til.light_iot_cloud.service.LightDataService;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class LightController {

    @Resource
    private LightDataService lightDataService;

    @Resource
    private DetectionKeyframeService detectionKeyframeService;

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
    public List<DetectionKeyframe> detectionKeyframes(Light light,@Argument @Nullable TimeRange timeRange) {

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
        return detectionKeyframeService.list(listQuery);
    }


    @SchemaMapping(typeName = "Light")
    public Result<Void> updata(Light light, @Argument LightData lightDataInput) {
        lightDataInput.setLightId(light.getId());
        return Result.ofBool(lightDataService.save(lightDataInput));
    }
}

