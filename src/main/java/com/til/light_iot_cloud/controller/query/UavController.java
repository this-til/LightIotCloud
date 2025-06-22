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

@Controller
public class UavController {

    @Resource
    private DeviceRunManager deviceRunManager;

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
