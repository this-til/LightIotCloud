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
    default Map<String, DetectionModel> ensureExistence(Set<String> set, Long userId) {

        Map<String, DetectionModel> collect = list(
                new LambdaQueryWrapper<DetectionModel>()
                        .eq(DetectionModel::getUserId, userId)
        )
                .stream()
                .collect(Collectors.toMap(DetectionModel::getName, m -> m));


        Set<String> existence = set.stream()
                .filter(s -> !collect.containsKey(s))
                .collect(Collectors.toSet());

        if (!existence.isEmpty()) {
            saveBatch(
                    existence
                            .stream()
                            .map(
                                    s -> {
                                        DetectionModel detectionModel = new DetectionModel();
                                        detectionModel.setName(s);
                                        detectionModel.setUserId(userId);
                                        return detectionModel;
                                    }
                            )
                            .collect(Collectors.toList())
            );
        }

        List<DetectionModel> list = list(
                new LambdaQueryWrapper<DetectionModel>()
                        .eq(DetectionModel::getUserId, userId)
                        .in(DetectionModel::getName, existence)
        );

        for(DetectionModel detectionModel : list) {
            collect.put(detectionModel.getName(), detectionModel);
        }

        return collect;
    }
}
