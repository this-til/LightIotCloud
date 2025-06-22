package com.til.light_iot_cloud.controller.mutation;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.data.Result;
import com.til.light_iot_cloud.data.UavState;
import com.til.light_iot_cloud.event.LightStateReportEvent;
import com.til.light_iot_cloud.event.UavStateReportEvent;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UavMutationController {

    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    @SchemaMapping(typeName = "UavMutation")
    public Result<Void> reportState(Device uav, @Argument UavState uavState) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(uav.getId());

        if (deviceContext == null) {
            throw new IllegalArgumentException("No device context found for light: " + uav.getId());
        }

        if (!(deviceContext instanceof DeviceContext.UavContext uavContext)) {
            throw new IllegalArgumentException("device context is not a LightContext");
        }

        uavContext.setUavState(uavState);

        sinkEventHolder.publishEvent(new UavStateReportEvent(uav, uavState));

        return Result.successful();
    }

}
