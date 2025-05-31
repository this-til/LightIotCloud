package com.til.light_iot_cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Car;
import com.baomidou.mybatisplus.extension.service.IService;
import com.til.light_iot_cloud.data.Light;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author cat
 * @description 针对表【car】的数据库操作Service
 * @createDate 2025-05-30 20:16:00
 */
public interface CarService extends IService<Car> {

    default List<Car> getCarsByUser(Long userId) {
        return list(
                new LambdaQueryWrapper<Car>()
                        .eq(Car::getUserId, userId)
        );
    }

    @Nullable
    default Car getCarByName(Long userId, String name) {
        return getOne(
                new LambdaQueryWrapper<Car>()
                        .eq(Car::getUserId, userId)
                        .eq(Car::getName, name)
        );
    }

    @Nullable
    default Car getCarById(Long userId, Long id) {
        return getOne(
                new LambdaQueryWrapper<Car>()
                        .eq(Car::getUserId, userId)
                        .eq(Car::getId, id)
        );
    }


}
