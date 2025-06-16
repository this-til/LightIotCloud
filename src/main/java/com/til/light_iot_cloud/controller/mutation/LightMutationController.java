package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.DetectionInput;
import com.til.light_iot_cloud.data.input.DetectionItemInput;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.*;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
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
    private SinkEventHolder sinkEventHolder;

    @Resource
    private DeviceMutationController deviceMutationController;

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> reportUpdate(Device light, @Argument LightData lightDataInput) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(light.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for light: " + light.getId());
        }

        if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
            throw new IllegalArgumentException("device context is not a LightContext");
        }

        lightDataInput.setLightId(light.getId());
        sinkEventHolder.publishEvent(new LightDataReportEvent(light.getId(), lightDataInput));

        lightDataInput.setPm25(lightDataInput.getPm2_5());

        return Result.ofBool(lightDataService.save(lightDataInput));
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> reportState(Device light, @Argument LightState lightState) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(light.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for light: " + light.getId());
        }

        if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
            throw new IllegalArgumentException("device context is not a LightContext");
        }

        lightContext.setLightState(lightState);

        sinkEventHolder.publishEvent(new LightStateReportEvent(light.getId(), lightState));

        return Result.successful();
    }

    @SchemaMapping(typeName = "LightMutation")
    @Transactional
    public Result<Void> reportDetection(Device light, @Argument DetectionInput detectionInput) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(light.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for light: " + light.getId());
        }

        if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
            throw new IllegalArgumentException("device context is not a LightContext");
        }

        DetectionKeyframe detectionKeyframe = new DetectionKeyframe();

        detectionKeyframe.setUserId(light.getUserId());
        detectionKeyframe.setDeviceId(light.getId());
        detectionKeyframeService.save(detectionKeyframe);

        imageStorageService.storeImage(detectionInput.getImage(), detectionKeyframe.getId().toString());

        if (detectionInput.getItems().isEmpty()) {
            return Result.successful();
        }

        List<DetectionItemInput> items = detectionInput.getItems();

        Map<String, List<DetectionItemInput>> modelMap = items.stream()
                .collect(Collectors.groupingBy(DetectionItemInput::getModel));

        Map<String, DetectionModel> stringDetectionModelMap = detectionModelService.ensureExistence(modelMap.keySet().stream().toList(), light.getUserId());

        List<Detection> detectionList = new ArrayList<>();

        for(Map.Entry<String, List<DetectionItemInput>> entry : modelMap.entrySet()) {

            DetectionModel detectionModel = stringDetectionModelMap.get(entry.getKey());

            List<DetectionItemInput> value = entry.getValue();

            if (value.isEmpty()) {
                continue;
            }

            Map<String, List<DetectionItemInput>> itemMap = value.stream()
                    .collect(Collectors.groupingBy(DetectionItemInput::getItem));

            Map<String, DetectionItem> stringDetectionItemMap = detectionItemService.ensureExistence(itemMap.keySet().stream().toList(), detectionModel.getId());

            for(Map.Entry<String, List<DetectionItemInput>> itemEntry : itemMap.entrySet()) {

                DetectionItem detectionItem = stringDetectionItemMap.get(itemEntry.getKey());

                for(DetectionItemInput detectionItemInput : itemEntry.getValue()) {

                    Detection detection = detectionItemInput.asDetection();
                    detection.setKeyframeId(detectionKeyframe.getId());
                    detection.setItemId(detectionItem.getId());
                    detectionList.add(detection);

                }

            }

        }

        detectionService.saveBatch(detectionList);
        detectionKeyframe.setDetections(detectionList);
        detectionKeyframe.setTime(OffsetDateTime.now());

        sinkEventHolder.publishEvent(new LightDetectionReportEvent(light.getId(), detectionKeyframe));

        return Result.ofBool(true);
    }


    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> sustainedReportDetection(Device light, @Argument List<DetectionItemInput> items) {

        sinkEventHolder.publishEvent(
                new LightSustainedDetectionReportEvent(
                        light.getId(),
                        items.stream().map(DetectionItemInput::asDetection).toList()
                )
        );

        return Result.successful();
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setGear(Device light, @Argument Integer value) {
        return deviceMutationController.setConfiguration(light, "Device.Gear", value.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setAutomaticGear(Device light, @Argument Boolean value) {
        return deviceMutationController.setConfiguration(light, "Device.Switch", value.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setSustainedDetection(Device light, @Argument String modelName) {
        return deviceMutationController.commandDown(light, "Detection.Sustained", modelName);
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> closeSustainedDetection(Device light) {
        return deviceMutationController.commandDown(light, "Detection.Sustained", "null");
    }

}
