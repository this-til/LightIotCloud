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
 * @author TIL
 */
@Controller
public class CarController {

    @Resource
    private DeviceRunManager deviceRunManager;

    /**
     * 获取车辆实时状态
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
