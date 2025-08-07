package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.DetectionInput;
import com.til.light_iot_cloud.data.input.DetectionItemInput;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.CommandDownEvent;
import com.til.light_iot_cloud.event.LightDetectionReportEvent;
import com.til.light_iot_cloud.event.LightSustainedDetectionReportEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 通用设备变更控制器
 * 
 * 提供所有设备类型的通用变更操作，作为 GraphQL DeviceMutation 类型的实现。
 * 是设备管理的核心控制器，负责设备配置、命令下发、检测数据处理等功能，
 * 为不同类型的设备提供统一的操作接口。
 * 
 * 主要功能模块：
 * - 设备配置管理：设置和更新设备配置参数
 * - 命令下发：向设备发送各种控制命令
 * - 检测数据处理：处理设备上报的检测结果
 * - 图像存储：管理检测关键帧图像的存储
 * - 设备类型转换：提供设备类型安全转换
 * 
 * 数据处理流程：
 * - 接收设备上报的检测数据
 * - 解析并分类检测项目
 * - 确保检测模型和项目的存在性
 * - 持久化存储检测结果
 * - 实时推送检测事件
 * 
 * @author TIL
 */
@Controller
public class DeviceMutationController {
    @Resource
    private DeviceService deviceService;

    @Resource
    private SinkEventHolder sinkEventHolder;

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

    /**
     * 设置设备配置
     * 
     * 更新设备的配置参数，配置更改会通过事件机制推送到设备。
     * 支持各种设备参数的动态配置和实时更新。
     * 
     * @param device 目标设备
     * @param key 配置键名，标识具体的配置项
     * @param value 配置值，新的配置参数值
     * @return 操作结果，成功时返回 successful
     */
    @SchemaMapping(typeName = "DeviceMutation")
    public Result<Void> setConfiguration(Device device, @Argument String key, @Argument String value) {
        sinkEventHolder.publishEvent(new UpdateConfigurationEvent(device.getId(), key, value));
        return Result.successful();
    }

    /**
     * 发送设备命令
     * 
     * 向设备下发控制命令，是所有设备命令操作的统一入口。
     * 命令通过事件机制异步发送到目标设备。
     * 
     * @param device 目标设备
     * @param key 命令键，标识具体的命令类型
     * @param value 命令值，命令的参数或数据
     * @return 操作结果，成功时返回 successful
     */
    @SchemaMapping(typeName = "DeviceMutation")
    public Result<Void> commandDown(Device device, @Argument String key, @Argument String value) {
        sinkEventHolder.publishEvent(new CommandDownEvent(device.getId(), key, value));
        return Result.successful();
    }

    /**
     * 上报检测结果
     * 
     * 处理设备上报的检测数据，包括图像和检测项目信息。
     * 此方法负责完整的检测数据处理流程：
     * 1. 验证设备上下文
     * 2. 创建检测关键帧记录
     * 3. 存储检测图像
     * 4. 处理检测项目数据
     * 5. 确保检测模型和项目存在
     * 6. 批量保存检测结果
     * 7. 发送检测事件通知
     * 
     * 数据结构：
     * - 按检测模型分组检测项目
     * - 按检测项目类型进一步分组
     * - 确保数据库中模型和项目记录的存在性
     * 
     * @param light 灯光设备（检测设备）
     * @param detectionInput 检测输入数据，包含图像和检测项目列表
     * @return 操作结果，成功时返回 true
     * @throws IllegalArgumentException 当设备上下文无效时抛出
     */
    @SchemaMapping(typeName = "DeviceMutation")
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

    /**
     * 上报持续检测结果
     * 
     * 处理设备的持续检测数据，用于实时检测场景。
     * 与普通检测不同，持续检测不保存到数据库，
     * 只通过事件机制实时推送给订阅者。
     * 
     * 适用场景：
     * - 实时监控和预警
     * - 临时检测任务
     * - 高频检测数据流
     * 
     * @param light 灯光设备（检测设备）
     * @param items 检测项目输入列表，包含检测结果数据
     * @return 操作结果，成功时返回 successful
     */
    @SchemaMapping(typeName = "DeviceMutation")
    public Result<Void> sustainedReportDetection(Device light, @Argument List<DetectionItemInput> items) {

        sinkEventHolder.publishEvent(
                new LightSustainedDetectionReportEvent(
                        light.getId(),
                        items.stream()
                                .map(DetectionItemInput::asDetection)
                                .toList()
                )
        );

        return Result.successful();
    }

    /**
     * 转换为灯光设备
     * 
     * 将通用设备对象安全转换为灯光设备类型。
     * 在操作前验证设备类型，确保类型安全。
     * 
     * @param device 要转换的设备对象
     * @return 相同的设备对象（类型验证后）
     * @throws IllegalArgumentException 当设备类型不是灯光设备时抛出
     */
    @SchemaMapping(typeName = "DeviceMutation")
    public Device asLight(Device device) {
        if (!device.getDeviceType().equals(DeviceType.LIGHT)) {
            throw new IllegalArgumentException("Device type is not Light");
        }
        return device;
    }

    /**
     * 转换为车辆设备
     * 
     * 将通用设备对象安全转换为车辆设备类型。
     * 在操作前验证设备类型，确保类型安全。
     * 
     * @param device 要转换的设备对象
     * @return 相同的设备对象（类型验证后）
     * @throws IllegalArgumentException 当设备类型不是车辆设备时抛出
     */
    @SchemaMapping(typeName = "DeviceMutation")
    public Device asCar(Device device) {
        if (!device.getDeviceType().equals(DeviceType.CAR)) {
            throw new IllegalArgumentException("Device type is not Car");
        }
        return device;
    }
}
