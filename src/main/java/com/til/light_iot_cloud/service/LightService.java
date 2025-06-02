package com.til.light_iot_cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Car;
import com.til.light_iot_cloud.data.Light;
import com.baomidou.mybatisplus.extension.service.IService;
import com.til.light_iot_cloud.data.User;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author cat
 * @description 针对表【light】的数据库操作Service
 * @createDate 2025-05-30 20:16:17
 */
public interface LightService extends IService<Light> {

    default List<Light> getLightsByUser(Long userId) {
        return list(
                new LambdaQueryWrapper<Light>()
                        .eq(Light::getUserId, userId)
        );
    }

    @Nullable
    default Light getLightByName(Long userId, String name) {
        return getOne(
                new LambdaQueryWrapper<Light>()
                        .eq(Light::getUserId, userId)
                        .eq(Light::getName, name)
        );
    }

    @Nullable
    default Light getLightById(Long userId, Long id) {
        return getOne(
                new LambdaQueryWrapper<Light>()
                        .eq(Light::getUserId, userId)
                        .eq(Light::getId, id)
        );
    }


    default Light registerLight(Long userId, String name) {
        Light light = getLightByName(userId, name);

        if (light != null) {
            throw new SecurityException("Light already exists");
        }

        light = new Light();
        light.setName(name);
        light.setUserId(userId);

        if (!save(light)) {
            throw new SecurityException("save failed");
        }

        return light;
    }

    default Light existLight(Long userId, String name) {
        Light light = getLightByName(userId, name);

        if (light != null) {
            return light;
        }

        return registerLight(userId, name);
    }

}
