package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.CarState;
import com.til.light_iot_cloud.data.Device;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 车辆设备查询控制器
 * 
 * 提供车辆设备相关的数据查询功能，作为 GraphQL Car 类型的字段解析器。
 * 主要负责车辆设备的实时状态信息查询。
 * 
 * 主要功能：
 * - 车辆实时状态查询
 * - 车辆运行数据获取
 * - 车辆设备上下文管理
 * 
 * 状态信息：
 * - 位置和导航数据
 * - 运动状态（速度、方向）
 * - 电池和电源状态
 * - 传感器数据
 * 
 * @author TIL
 */
@Controller
public class CarController {

    @Resource
    private DeviceRunManager deviceRunManager;

    /**
     * 获取车辆实时状态
     * 
     * 从设备运行管理器中获取指定车辆设备的当前状态信息，
     * 包括位置、速度、方向、电池状态等实时数据。
     * 
     * 状态数据包含：
     * - GPS位置信息
     * - 运动状态（速度、方向、加速度）
     * - 电池电量和充电状态
     * - 车辆工作模式
     * - 传感器读数
     * 
     * @param car 车辆设备对象
     * @return 车辆状态对象，如果设备离线或上下文无效则返回null
     */
    @SchemaMapping(typeName = "Car")
    public CarState carState(Device car) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(car.getId());

        if (deviceContext == null) {
            return null;
        }

        if (!(deviceContext instanceof DeviceContext.CarContext carContext)) {
            return null;
        }

        return carContext.getCarState();
    }

}
