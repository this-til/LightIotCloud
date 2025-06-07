package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.LightContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.DetectionInput;
import com.til.light_iot_cloud.data.input.DetectionItemInput;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.LightDataReportEvent;
import com.til.light_iot_cloud.event.LightStateReportEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private ImageStorageService imageStorageService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> reportUpdate(Light light, @Argument LightData lightDataInput) {
        LightContext lightContext = deviceRunManager.getLightContext(light.getId());

        if (lightContext == null) {
            return Result.error("light not online");
        }

        lightDataInput.setLightId(light.getId());
        applicationEventPublisher.publishEvent(new LightDataReportEvent(this, light.getId(), lightDataInput));
        return Result.ofBool(lightDataService.save(lightDataInput));
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> reportState(Light light, @Argument LightState lightState) {
        LightContext lightContext = deviceRunManager.getLightContext(light.getId());

        if (lightContext == null) {
            return Result.error("light not online");
        }

        lightContext.setLightState(lightState);

        applicationEventPublisher.publishEvent(new LightStateReportEvent(this, light.getId(), lightState));

        return Result.successful();
    }

    @SchemaMapping(typeName = "LightMutation")
    @Transactional
    public Result<Void> reportDetection(Light light, @Argument DetectionInput detectionInput) {

        DetectionKeyframe detectionKeyframe = new DetectionKeyframe();

        String url = imageStorageService.storeImage(detectionInput.getImage());

        detectionKeyframe.setLightId(light.getId());
        detectionKeyframe.setUrl(url);
        detectionKeyframeService.save(detectionKeyframe);

        if (detectionInput.getItems().isEmpty()) {
            return Result.successful();
        }

        List<DetectionItemInput> items = detectionInput.getItems();

        Map<String, List<DetectionItemInput>> modelMap = items.stream()
                .collect(Collectors.groupingBy(DetectionItemInput::getModel));

        Map<String, DetectionModel> stringDetectionModelMap = detectionModelService.ensureExistence(modelMap.keySet(), light.getUserId());

        List<Detection> detectionList = new ArrayList<>();

        for(Map.Entry<String, List<DetectionItemInput>> entry : modelMap.entrySet()) {

            DetectionModel detectionModel = stringDetectionModelMap.get(entry.getKey());

            List<DetectionItemInput> value = entry.getValue();

            if (value.isEmpty()) {
                continue;
            }

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
        applicationEventPublisher.publishEvent(new UpdateConfigurationEvent(this, DeviceType.LIGHT, light.getId(), key, value));
        return Result.successful();
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setGear(Light light, @Argument Integer value) {
        return setConfiguration(light, "Device.Gear", value.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setAutomaticGear(Light light, @Argument Boolean value) {
        return setConfiguration(light, "Device.Switch", value.toString());
    }


}
