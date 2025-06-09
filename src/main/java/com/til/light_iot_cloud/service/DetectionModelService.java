package com.til.light_iot_cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.DetectionModel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.til.light_iot_cloud.data.Pair;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cat
 * @description 针对表【detection_model】的数据库操作Service
 * @createDate 2025-05-31 21:06:52
 */
public interface DetectionModelService extends IService<DetectionModel> {

    @Transactional
    default Map<String, DetectionModel> ensureExistence(List<String> set, Long userId) {
        Map<String, DetectionModel> collect = list(
                new LambdaQueryWrapper<DetectionModel>()
                        .eq(DetectionModel::getUserId, userId)
                        .in(DetectionModel::getName, set)
        )
                .stream()
                .collect(Collectors.toMap(DetectionModel::getName, m -> m));


        List<String> existence = set.stream()
                .filter(s -> !collect.containsKey(s))
                .toList();

        for(String s : existence) {
            DetectionModel detectionModel = new DetectionModel();
            detectionModel.setName(s);
            detectionModel.setUserId(userId);
            save(detectionModel);
            collect.put(s, detectionModel);
        }

        return collect;
    }
}
