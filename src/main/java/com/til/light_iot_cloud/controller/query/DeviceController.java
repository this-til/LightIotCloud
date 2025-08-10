package com.til.light_iot_cloud.controller.query;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;

/**
 * 通用设备查询控制器
 * <p>
 * 提供所有设备类型的通用查询功能，作为 GraphQL Device 类型的字段解析器。
 * 主要负责设备的基本信息查询、在线状态检查和设备类型转换等操作。
 * <p>
 * 主要功能：
 * - 设备在线状态检查
 * - 设备类型安全转换
 * - 通用设备属性查询
 * <p>
 * 类型转换：
 * - 将通用设备转换为特定类型（车辆、灯光）
 * - 提供类型安全的转换验证
 * - 支持GraphQL联合类型解析
 * 
 * @author TIL
 */
@Controller
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @Resource
    private DeviceRunManager deviceRunManager;

    /**
     * 检查设备在线状态
     * <p>
     * 通过检查设备运行管理器中的设备上下文来判断设备是否在线。
     * 如果设备有活跃的上下文，则认为设备在线；否则认为设备离线。
     * 
     * @param device 要检查的设备对象
     * @return 设备是否在线
     *         - true: 设备在线，有活跃连接
     *         - false: 设备离线，无活跃连接
     */
    @SchemaMapping(typeName = "Device")
    public boolean online(Device device) {
        return deviceRunManager.getDeviceContext(device.getId()) != null;
    }

    /**
     * 转换为车辆设备
     * <p>
     * 将通用设备对象安全转换为车辆设备类型。
     * 在转换前验证设备类型，确保类型安全性。
     * 
     * @param device 要转换的设备对象
     * @return 相同的设备对象（类型验证后）
     * @throws IllegalArgumentException 当设备类型不是车辆设备时抛出
     */
    @SchemaMapping(typeName = "Device")
    public Device asCar(Device device) {
        if (device.getDeviceType() != DeviceType.CAR) {
            throw new IllegalArgumentException("Device type is not Car");
        }
        return device;
    }

    /**
     * 转换为灯光设备
     * <p>
     * 将通用设备对象安全转换为灯光设备类型。
     * 在转换前验证设备类型，确保类型安全性。
     * 
     * @param device 要转换的设备对象
     * @return 相同的设备对象（类型验证后）
     * @throws IllegalArgumentException 当设备类型不是灯光设备时抛出
     */
    @SchemaMapping(typeName = "Device")
    public Device asLight(Device device) {
        if (device.getDeviceType() != DeviceType.LIGHT) {
            throw new IllegalArgumentException("Device type is not Light");
        }
        return device;
    }

}
