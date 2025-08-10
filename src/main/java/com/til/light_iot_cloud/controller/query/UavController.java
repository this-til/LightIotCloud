package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.data.UavState;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 无人机设备查询控制器
 * <p>
 * 提供无人机设备相关的数据查询功能，作为 GraphQL Uav 类型的字段解析器。
 * 主要负责无人机设备的实时状态信息查询，包括飞行状态、位置信息等。
 * <p>
 * 主要功能：
 * - 无人机实时状态查询
 * - 飞行数据获取
 * - 无人机设备上下文管理
 * <p>
 * 状态信息：
 * - GPS位置和高度信息
 * - 飞行姿态（俯仰、偏航、翻滚）
 * - 电池电量和飞行时间
 * - 飞行模式和任务状态
 * - 传感器和相机数据
 * 
 * @author TIL
 */
@Controller
public class UavController {

    @Resource
    private DeviceRunManager deviceRunManager;

    /**
     * 获取无人机实时状态
     * <p>
     * 从设备运行管理器中获取指定无人机设备的当前状态信息，
     * 包括飞行状态、GPS位置、高度、姿态、电池状态等实时数据。
     * <p>
     * 状态数据包含：
     * - GPS坐标和飞行高度
     * - 飞行姿态（俯仰角、偏航角、翻滚角）
     * - 电池电量和剩余飞行时间
     * - 飞行模式（自动、手动、返航等）
     * - 任务执行状态
     * - 传感器和相机状态
     * 
     * @param authContext 认证上下文
     * @param uav 无人机设备对象
     * @return 无人机状态对象，如果设备离线或上下文无效则返回null
     */
    @SchemaMapping(typeName = "Uav")
    public UavState uavState(@ContextValue AuthContext authContext, Device uav) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(uav.getId());

        if (deviceContext == null) {
            return null;
        }

        if (!(deviceContext instanceof DeviceContext.UavContext uavContext)) {
            return null;
        }

        return uavContext.getUavState();
    }

}
