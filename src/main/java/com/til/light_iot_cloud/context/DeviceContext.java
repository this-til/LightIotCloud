package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.data.CarState;
import com.til.light_iot_cloud.data.Device;
import com.til.light_iot_cloud.data.LightState;
import com.til.light_iot_cloud.data.UavState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
public abstract class DeviceContext {
    Device device;

    protected DeviceContext() {
    }

    public static DeviceContext create(Device device) {
        switch (device.getDeviceType()) {
            case CAR -> {
                return new CarContext(device);
            }
            case LIGHT -> {
                return new LightContext(device);
            }
            default -> throw new IllegalArgumentException("Device type not supported: " + device.getDeviceType());
        }
    }


    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class LightContext extends DeviceContext {
        LightState lightState = new LightState();

        public LightContext(Device device) {
            super(device);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CarContext extends DeviceContext {
        CarState carState = new CarState();

        public CarContext(Device device) {
            super(device);
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class UavContext extends DeviceContext {
        UavState uavState = new UavState();

        public UavContext(Device device) {
            super(device);
        }
    }
}
