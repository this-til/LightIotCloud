package com.til.light_iot_cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.til.light_iot_cloud.enums.DeviceType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author cat
 * @description 针对表【device】的数据库操作Service
 * @createDate 2025-06-15 16:54:02
 */
public interface DeviceService extends IService<Device> {

    @Nullable
    default Device getDeviceById(Long id) {
        return this.getById(id);
    }

    @Nullable
    default Device getDeviceById(Long userId, Long id) {
        return this.getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, userId)
                        .eq(Device::getId, id)
        );
    }

    @Nullable
    default Device getDeviceById(Long userId, Long id, DeviceType deviceType) {
        return this.getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, userId)
                        .eq(Device::getId, id)
                        .eq(Device::getDeviceType, deviceType)
        );
    }

    default List<Device> getDeviceByUserId(Long userId) {
        return list(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, userId)
        );
    }

    @Nullable
    default Device getDeviceByName(Long userId, String name) {
        return getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, userId)
                        .eq(Device::getName, name)
        );
    }

    @Nullable
    default Device getDeviceByName(Long userId, String name, DeviceType deviceType) {
        return getOne(
                new LambdaQueryWrapper<Device>()
                        .eq(Device::getUserId, userId)
                        .eq(Device::getName, name)
                        .eq(Device::getDeviceType, deviceType)
        );
    }

    default Device existDevice(Long userId, String name, DeviceType deviceType) {
        Device deviceByName = getDeviceByName(userId, name, deviceType);
        if (deviceByName != null) {
            return deviceByName;
        }
        synchronized (this) {
            deviceByName = getDeviceByName(userId, name, deviceType);
            if (deviceByName != null) {
                return deviceByName;
            }

            Device device = new Device();
            device.setUserId(userId);
            device.setName(name);
            device.setDeviceType(deviceType);
            save(device);
            return device;
        }
    }

}
