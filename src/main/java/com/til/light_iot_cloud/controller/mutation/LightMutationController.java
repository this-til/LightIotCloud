package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.DetectionInput;
import com.til.light_iot_cloud.data.input.DetectionItemInput;
import com.til.light_iot_cloud.data.subscription.UpdateConfiguration;
import com.til.light_iot_cloud.service.*;
import com.til.light_iot_cloud.type.ISubscriptionType;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LightMutationController {
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
    private DeviceConnectionManager deviceConnectionManager;

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> reportUpdate(Light light, @Argument LightData lightDataInput) {
        lightDataInput.setLightId(light.getId());
        return Result.ofBool(lightDataService.save(lightDataInput));
    }

    @SchemaMapping(typeName = "LightMutation")
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

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setConfiguration(Light light, @Argument String key, @Argument String value) {

        Long id = light.getId();

        DeviceContext deviceContext = deviceConnectionManager.getPublisherByLightId(id);

        if (deviceContext == null) {
            return Result.error("light is not online");
        }

        List<Sinks.Many<UpdateConfiguration>> subscription = deviceContext.findSubscription(ISubscriptionType.updateConfiguration);

        if (subscription == null || subscription.isEmpty()) {
            return Result.error("no subscription found");
        }

        for(Sinks.Many<UpdateConfiguration> updateConfigurationMany : subscription) {
            updateConfigurationMany.tryEmitNext(new UpdateConfiguration(key, value));
        }

        return Result.ofBool(true);

    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setGear(Light light, @Argument Integer value) {
        return setConfiguration(light, "Device.Gear", value.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setSwitch(Light light, @Argument Boolean value) {
        return setConfiguration(light, "Device.Switch", value.toString());
    }



}
