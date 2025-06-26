package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.enums.LightCommandKey;
import com.til.light_iot_cloud.enums.PtzControl;
import com.til.light_iot_cloud.event.*;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

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
        lightDataInput.setPm25(lightDataInput.getPm2_5());

        sinkEventHolder.publishEvent(new LightDataReportEvent(light.getId(), lightDataInput));

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
    public Result<Void> commandDown(Device light, @Argument LightCommandKey key, @Argument String value) {
        return deviceMutationController.commandDown(light, key.getValue(), value);
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> ptzControl(Device light, @Argument PtzControl ptzControl) {
        return commandDown(light, LightCommandKey.CAMERA_PTZ_CONTROL, ptzControl.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setGear(Device light, @Argument Integer value) {
        return commandDown(light, LightCommandKey.DEVICE_GEAR, value.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setAutomaticGear(Device light, @Argument Boolean value) {
        return commandDown(light, LightCommandKey.DEVICE_SWITCH, value.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setRollingDoor(Device light, @Argument Boolean open) {
        return commandDown(light, LightCommandKey.DEVICE_ROLLING_DOOR, open.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setSustainedDetection(Device light, @Argument String modelName) {
        return commandDown(light, LightCommandKey.DETECTION_SUSTAINED, modelName);
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> closeSustainedDetection(Device light) {
        return commandDown(light, LightCommandKey.DETECTION_SUSTAINED, "null");
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> broadcastFile(Device light, @Argument String fileName) {
        return commandDown(light, LightCommandKey.BROADCAST_FILE, fileName);
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> broadcastStop(Device light) {
        return commandDown(light, LightCommandKey.BROADCAST_STOP, "null");
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setUavBaseStationCover(Device light, @Argument Boolean open) {
        return commandDown(light, LightCommandKey.UAV_BASE_STATION_COVER, open.toString());
    }

    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setUavBaseStationClamp(Device light, @Argument Boolean open) {
        return commandDown(light, LightCommandKey.UAV_BASE_STATION_CLAMP, open.toString());
    }
}
