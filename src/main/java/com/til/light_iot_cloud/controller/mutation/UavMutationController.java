package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.data.UavState;
import com.til.light_iot_cloud.enums.UavCommandKey;
import com.til.light_iot_cloud.event.LightStateReportEvent;
import com.til.light_iot_cloud.event.UavStateReportEvent;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 无人机设备变更控制器
 * <p>
 * 提供无人机设备的状态管理功能，作为 GraphQL UavMutation 类型的实现。
 * 主要负责无人机状态信息的上报和管理，包括飞行状态、位置信息、
 * 电池状态、传感器数据等。
 * <p>
 * 主要功能：
 * - 无人机状态信息上报和管理
 * - 无人机飞行数据实时推送
 * - 无人机设备上下文维护
 * <p>
 * 设备验证：
 * - 验证无人机设备上下文的有效性
 * - 确保操作的设备类型为无人机
 * - 维护无人机运行状态的一致性
 *
 * @author TIL
 */
@Controller
public class UavMutationController {

    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @Resource
    private DeviceMutationController deviceMutationController;

    /**
     * 上报无人机状态
     * <p>
     * 接收并处理无人机设备的状态信息，包括飞行状态、GPS位置、
     * 高度、姿态、电池电量、传感器数据等。状态信息会被更新到
     * 设备上下文中，并通过事件机制实时推送给订阅者。
     * <p>
     * 状态信息包含：
     * - 飞行位置和高度
     * - 飞行姿态（俯仰、偏航、翻滚）
     * - 电池状态和剩余电量
     * - 飞行模式和状态
     * - 传感器数据
     *
     * @param uav      无人机设备对象
     * @param uavState 无人机状态数据，包含设备的当前飞行状态
     * @return 操作结果，成功时返回 successful
     * @throws IllegalArgumentException 当设备上下文无效或类型不匹配时抛出
     */
    @SchemaMapping(typeName = "UavMutation")
    public Result<Void> reportState(Device uav, @Argument UavState uavState) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(uav.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for uav: " + uav.getId());
        }

        if (!(deviceContext instanceof DeviceContext.UavContext uavContext)) {
            throw new IllegalArgumentException("device context is not a UavContext");
        }

        uavContext.setUavState(uavState);

        sinkEventHolder.publishEvent(new UavStateReportEvent(uav, uavState));

        return Result.successful();
    }

    @SchemaMapping(typeName = "UavMutation")
    public Result<Void> commandDown(Device uav, @Argument UavCommandKey key, @Argument String value) {
        return deviceMutationController.commandDown(uav, key.getValue(), value);
    }

    @SchemaMapping(typeName = "UavMutation")
    public Result<Void> open(Device uav) {
        return commandDown(uav, UavCommandKey.OPEN, "null");
    }

    @SchemaMapping(typeName = "UavMutation")
    public Result<Void> close(Device uav) {
        return commandDown(uav, UavCommandKey.CLOSE, "null");
    }
}
