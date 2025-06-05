package com.til.light_iot_cloud.trigger;

import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.event.OperationCarEvent;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TestTrigger {

    @Resource
    private DeviceRunManager deviceRunManager;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public TestTrigger() {
        scheduler.scheduleAtFixedRate(this::sendOperationCar, 0, 300, TimeUnit.MILLISECONDS);
    }

    private void sendOperationCar() {
        deviceRunManager.getCarMap().forEach(
                (k, v) -> {
                    OperationCarInput operationCarInput = new OperationCarInput();
                    operationCarInput.setTranslationAdvance(true);
                    applicationEventPublisher.publishEvent(new OperationCarEvent(this, k, operationCarInput));
                }
        );
    }
}
