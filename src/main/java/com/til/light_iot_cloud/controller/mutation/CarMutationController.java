package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.CarCommandKey;
import com.til.light_iot_cloud.enums.LightCommandKey;
import com.til.light_iot_cloud.enums.OperationCar;
import com.til.light_iot_cloud.event.CarStateReportEvent;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 车辆设备变更控制器
 * <p>
 * 提供车辆设备的控制和状态管理功能，作为 GraphQL CarMutation 类型的实现。
 * 支持车辆的基本控制、状态上报、音频广播等操作，
 * 是车辆设备与系统交互的核心控制器。
 * <p>
 * 主要功能：
 * - 车辆命令下发与控制
 * - 车辆状态信息上报和管理
 * - 车辆操作指令执行（前进、后退、转向等）
 * - 车辆音频广播控制
 * <p>
 * 设备验证：
 * - 验证车辆设备上下文的有效性
 * - 确保操作的设备类型为车辆
 * - 维护车辆运行状态的一致性
 * 
 * @author TIL
 */
@Controller
public class CarMutationController {

    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @Resource
    private DeviceMutationController deviceMutationController;

    /**
     * 发送车辆控制命令
     * <p>
     * 向车辆设备下发各种控制命令，包括运动控制、功能设置等。
     * 命令通过设备连接管理器发送到目标车辆设备。
     * 
     * @param car 目标车辆设备
     * @param key 车辆命令键，定义具体的命令类型
     * @param value 命令值，命令的参数或数据
     * @return 操作结果，表示命令发送状态
     */
    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> commandDown(Device car, @Argument CarCommandKey key, @Argument String value) {
        return deviceMutationController.commandDown(car, key.getValue(), value);
    }

    /**
     * 上报车辆状态
     * <p>
     * 接收并处理车辆设备的状态信息，包括位置、速度、方向、
     * 电池状态、传感器数据等。状态信息会被更新到设备上下文中，
     * 并通过事件机制实时推送给订阅者。
     * 
     * @param car 车辆设备对象
     * @param carState 车辆状态数据，包含设备的当前运行状态
     * @return 操作结果，成功时返回 successful
     * @throws RuntimeException 当设备上下文无效或类型不匹配时抛出
     */
    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> reportState(Device car, @Argument CarState carState) {

        DeviceContext deviceContext = deviceRunManager.getDeviceContext(car.getId());

        if (deviceContext == null) {
            throw new RuntimeException("Device context not found");
        }

        if (!(deviceContext instanceof DeviceContext.CarContext carContext)) {
            throw new RuntimeException("Device context is not a car context");
        }

        carContext.setCarState(carState);
        sinkEventHolder.publishEvent(new CarStateReportEvent(car.getId(), carState));
        return Result.successful();
    }

    /**
     * 执行车辆操作指令
     * <p>
     * 执行预定义的车辆操作，如前进、后退、左转、右转、停止等基本运动控制。
     * 这是一个高级接口，将具体的操作转换为相应的底层命令。
     * 
     * @param car 目标车辆设备
     * @param operationCar 车辆操作类型，包含具体的运动指令
     * @return 操作结果
     */
    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> operationCar(Device car, @Argument OperationCar operationCar) {
        return commandDown(car, CarCommandKey.OPERATION, operationCar.name());
    }

    /**
     * 播放车辆广播文件
     * <p>
     * 控制车辆设备播放指定的音频文件，用于语音提示、警告广播等。
     * 文件应存储在车辆设备的本地存储中。
     * 
     * @param car 目标车辆设备
     * @param fileName 音频文件名，车辆本地存储的文件
     * @return 操作结果
     */
    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> broadcastFile(Device car, @Argument String fileName) {
        return commandDown(car, CarCommandKey.BROADCAST_FILE, fileName);
    }

    /**
     * 停止车辆广播播放
     * <p>
     * 停止车辆设备当前正在播放的音频广播。
     * 
     * @param car 目标车辆设备
     * @return 操作结果
     */
    @SchemaMapping(typeName = "CarMutation")
    public Result<Void> broadcastStop(Device car) {
        return commandDown(car, CarCommandKey.BROADCAST_STOP, "null");
    }

}
