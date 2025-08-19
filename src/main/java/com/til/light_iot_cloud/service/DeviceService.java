package com.til.light_iot_cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.til.light_iot_cloud.data.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.til.light_iot_cloud.enums.DeviceType;
import org.jetbrains.annotations.Nullable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

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
        LambdaQueryWrapper<Device> eq = new LambdaQueryWrapper<Device>();
        eq.eq(Device::getUserId, userId);

        if (deviceType != null) {
            eq.eq(Device::getDeviceType, deviceType);
        }

        eq.eq(Device::getId, id);

        return this.getOne(eq);
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
    default Device getDeviceByName(Long userId, String name, @Nullable DeviceType deviceType) {

        LambdaQueryWrapper<Device> eq = new LambdaQueryWrapper<Device>();

        eq.eq(Device::getUserId, userId);

        if (deviceType != null) {
            eq.eq(Device::getDeviceType, deviceType);
        }

        eq.eq(Device::getName, name);

        return getOne(eq);
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

    /**
     * 批量更新设备的最后更新时间
     *
     * @param deviceIds 设备ID集合
     */
    default void batchUpdateLastUpdateTime(Set<Long> deviceIds) {
        if (deviceIds == null || deviceIds.isEmpty()) {
            return;
        }

        this.update(
                new LambdaUpdateWrapper<Device>()
                        .set(Device::getUpdatedAt, OffsetDateTime.now())
                        .in(Device::getId, deviceIds)
        );
    }
}
