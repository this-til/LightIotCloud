package com.til.light_iot_cloud.handler;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.controller.mutation.CarMutationController;
import com.til.light_iot_cloud.controller.mutation.LightMutationController;
import com.til.light_iot_cloud.controller.query.LightController;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.enums.CarCommandKey;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LightCommandKey;
import com.til.light_iot_cloud.event.LightSustainedDetectionReportEvent;
import com.til.light_iot_cloud.service.DetectionService;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.graphql.server.WebSocketSessionInfo;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

//@Component
public class ProcessHandler {

    @Resource
    private SinkEventHolder sinkEventHolder;

    @Resource
    private LightMutationController lightMutationController;

    @Resource
    private DeviceService deviceService;

    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private CarMutationController carMutationController;

    @PostConstruct
    public void init() {
        sinkEventHolder.getSinks(LightSustainedDetectionReportEvent.class)
                .asFlux()
                .subscribe(
                        event -> {

                            Device device = deviceService.getById(event.getLightId());
                            if (device == null) {
                                return;
                            }
                            if (device.getDeviceType() != DeviceType.LIGHT) {
                                return;
                            }

                            DeviceContext deviceContext = deviceRunManager.getDeviceContext(event.getLightId());

                            if (deviceContext == null) {
                                return;
                            }

                            if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
                                return;
                            }

                            if (!lightContext.isAllowedDispatched()) {
                                return;
                            }

                            if (
                                    event.getDetections().stream()
                                            .filter(d -> Objects.equals(d.getModel(), "火灾"))
                                            .noneMatch(d -> Objects.equals(d.getItem(), "火"))
                            ) {
                                return;
                            }

                            lightContext.setAllowedDispatched(false);

                            lightMutationController.commandDown(device, LightCommandKey.DISPATCH, "null");


                            deviceRunManager.getDeviceContextMap().values().stream()
                                    .filter(e -> e.getDevice().getDeviceType() != DeviceType.CAR)
                                    .forEach(e -> carMutationController.commandDown(e.getDevice(), CarCommandKey.DISPATCH, "null"));

                        }
                );
    }


}
