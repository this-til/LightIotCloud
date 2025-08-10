package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.enums.AlarmDialogueOperateType;
import com.til.light_iot_cloud.enums.LightCommandKey;
import com.til.light_iot_cloud.enums.PtzControl;
import com.til.light_iot_cloud.event.*;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 灯光设备变更控制器
 * <p>
 * 提供灯光设备的全面控制和数据管理功能，作为 GraphQL LightMutation 类型的实现。
 * 支持设备状态上报、命令下发、设备控制、检测管理等多种操作，
 * 是灯光设备与系统交互的核心控制器。
 * <p>
 * 主要功能模块：
 * - 数据上报：灯光数据、状态信息上报
 * - 设备控制：PTZ控制、档位设置、自动模式切换
 * - 硬件控制：卷帘门、无人机基站等外设控制
 * - 检测管理：持续检测启停、报警对话管理
 * - 音频广播：文件播放、广播控制
 * - 调度管理：设备调度权限设置
 * <p>
 * 设备验证：
 * - 验证设备上下文的有效性
 * - 确保操作的设备类型正确
 * - 维护设备运行状态的一致性
 * 
 * @author TIL
 */
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

    /**
     * 上报灯光数据
     * <p>
     * 接收并处理灯光设备上报的传感器数据，包括环境监测数据、设备状态等。
     * 数据将被持久化存储并通过事件机制实时推送给订阅者。
     * 
     * @param light 灯光设备对象
     * @param lightDataInput 灯光数据输入，包含各种传感器读数
     *                       - PM2.5数据会被同步到pm25字段
     *                       - 设备ID会被自动设置
     * @return 操作结果，成功时返回数据保存状态
     * @throws IllegalArgumentException 当设备上下文无效时抛出
     */
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

    /**
     * 上报灯光状态
     * <p>
     * 接收并处理灯光设备的状态信息，包括开关状态、亮度、工作模式等。
     * 状态信息会被更新到设备上下文中，并实时推送给订阅者。
     * 
     * @param light 灯光设备对象
     * @param lightState 灯光状态数据，包含设备的当前运行状态
     * @return 操作结果，成功时返回 successful
     * @throws IllegalArgumentException 当设备上下文无效时抛出
     */
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

    /**
     * 发送设备命令
     * <p>
     * 向灯光设备下发控制命令，支持各种设备操作和配置。
     * 命令通过设备连接管理器发送到目标设备。
     * 
     * @param light 目标灯光设备
     * @param key 命令键，定义具体的命令类型
     * @param value 命令值，命令的参数或数据
     * @return 操作结果，表示命令发送状态
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> commandDown(Device light, @Argument LightCommandKey key, @Argument String value) {
        return deviceMutationController.commandDown(light, key.getValue(), value);
    }

    /**
     * PTZ 云台控制
     * <p>
     * 控制灯光设备上的摄像头云台，支持上下左右旋转、变焦等操作。
     * 
     * @param light 目标灯光设备
     * @param ptzControl PTZ控制命令，包含具体的云台操作指令
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> ptzControl(Device light, @Argument PtzControl ptzControl) {
        return commandDown(light, LightCommandKey.CAMERA_PTZ_CONTROL, ptzControl.toString());
    }

    /**
     * 设置设备档位
     * <p>
     * 调整灯光设备的工作档位，控制设备的功率和亮度等级。
     * 
     * @param light 目标灯光设备
     * @param value 档位值，通常为数字等级
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setGear(Device light, @Argument Integer value) {
        return commandDown(light, LightCommandKey.DEVICE_GEAR, value.toString());
    }

    /**
     * 设置自动档位模式
     * <p>
     * 启用或禁用设备的自动档位调节功能，
     * 启用时设备会根据环境条件自动调整工作档位。
     * 
     * @param light 目标灯光设备
     * @param value 是否启用自动档位模式
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setAutomaticGear(Device light, @Argument Boolean value) {
        return commandDown(light, LightCommandKey.DEVICE_SWITCH, value.toString());
    }

    /**
     * 控制卷帘门
     * <p>
     * 控制灯光设备上的卷帘门开关，用于设备保护或环境调节。
     * 
     * @param light 目标灯光设备
     * @param open 是否打开卷帘门
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setRollingDoor(Device light, @Argument Boolean open) {
        return commandDown(light, LightCommandKey.DEVICE_ROLLING_DOOR, open.toString());
    }

    /**
     * 启动持续检测
     * <p>
     * 使用指定的检测模型启动设备的持续检测功能，
     * 设备将持续进行图像识别和分析。
     * 
     * @param light 目标灯光设备
     * @param modelName 检测模型名称，指定使用的AI模型
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setSustainedDetection(Device light, @Argument String modelName) {
        return commandDown(light, LightCommandKey.DETECTION_SUSTAINED, modelName);
    }

    /**
     * 停止持续检测
     * <p>
     * 停止设备当前的持续检测功能，设备将停止图像分析。
     * 
     * @param light 目标灯光设备
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> closeSustainedDetection(Device light) {
        return commandDown(light, LightCommandKey.DETECTION_SUSTAINED, "null");
    }

    /**
     * 播放广播文件
     * <p>
     * 控制设备播放指定的音频文件，用于语音广播或提示。
     * 
     * @param light 目标灯光设备
     * @param fileName 音频文件名，设备本地存储的文件
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> broadcastFile(Device light, @Argument String fileName) {
        return commandDown(light, LightCommandKey.BROADCAST_FILE, fileName);
    }

    /**
     * 停止广播播放
     * <p>
     * 停止设备当前正在播放的音频广播。
     * 
     * @param light 目标灯光设备
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> broadcastStop(Device light) {
        return commandDown(light, LightCommandKey.BROADCAST_STOP, "null");
    }

    /**
     * 控制无人机基站舱盖
     * <p>
     * 控制集成在灯光设备上的无人机基站舱盖开关，
     * 用于无人机的起飞和降落。
     * 
     * @param light 目标灯光设备
     * @param open 是否打开舱盖
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setUavBaseStationCover(Device light, @Argument Boolean open) {
        return commandDown(light, LightCommandKey.UAV_BASE_STATION_COVER, open.toString());
    }

    /**
     * 控制无人机基站夹具
     * <p>
     * 控制集成在灯光设备上的无人机基站夹具，
     * 用于固定或释放无人机。
     * 
     * @param light 目标灯光设备
     * @param open 是否打开夹具
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setUavBaseStationClamp(Device light, @Argument Boolean open) {
        return commandDown(light, LightCommandKey.UAV_BASE_STATION_CLAMP, open.toString());
    }

    /**
     * 设置调度许可状态
     * <p>
     * 设置设备是否允许被调度系统调度。
     * 禁用时，设备将不会响应全局调度命令。
     * 
     * @param light 目标灯光设备
     * @param activation 是否允许被调度
     * @return 操作结果，成功时返回 successful
     * @throws IllegalArgumentException 当设备上下文无效时抛出
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> setAllowedDispatched(Device light, @Argument Boolean activation) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(light.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for light: " + light.getId());
        }

        if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
            throw new IllegalArgumentException("device context is not a LightContext");
        }

        lightContext.setAllowedDispatched(activation);
        return Result.successful();
    }

    /**
     * 请求报警对话
     * <p>
     * 当设备检测到异常情况时，请求启动报警对话流程。
     * 系统将发送报警对话请求事件给相关订阅者。
     * 
     * @param light 发起请求的灯光设备
     * @return 操作结果，成功时返回 successful
     * @throws IllegalArgumentException 当设备上下文无效时抛出
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> requestAlarmDialogue(Device light) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(light.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for light: " + light.getId());
        }

        if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
            throw new IllegalArgumentException("device context is not a LightContext");
        }

        sinkEventHolder.publishEvent(new AlarmDialogueOperateEvent(light, AlarmDialogueOperateType.REQUEST));

        return Result.successful();
    }

    /**
     * 关闭报警对话
     * <p>
     * 结束当前的报警对话流程，设备将停止报警状态。
     * 同时发送对话关闭事件和设备关闭命令。
     * 
     * @param light 目标灯光设备
     * @return 操作结果
     */
    @SchemaMapping(typeName = "LightMutation")
    public Result<Void> closeAlarmDialogue(Device light) {
        sinkEventHolder.publishEvent(new AlarmDialogueOperateEvent(light, AlarmDialogueOperateType.CLOSE));
        return commandDown(light, LightCommandKey.CLOSE_ALARM_DIALOGUE, "null");
    }
}
