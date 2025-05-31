package com.til.light_iot_cloud.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.DetectionItem;
import com.baomidou.mybatisplus.extension.service.IService;
import com.til.light_iot_cloud.data.DetectionModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author cat
 * @description 针对表【detection_item】的数据库操作Service
 * @createDate 2025-05-31 20:33:34
 */
public interface DetectionItemService extends IService<DetectionItem> {

    @Transactional
    default Map<String, DetectionItem> ensureExistence(Set<String> set, Long modelId) {
        Map<String, DetectionItem> collect = list(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getModelId, modelId)
        )
                .stream()
                .collect(Collectors.toMap(DetectionItem::getName, m -> m));


        Set<String> existence = set.stream()
                .filter(s -> !collect.containsKey(s))
                .collect(Collectors.toSet());

        if (!existence.isEmpty()) {
            saveBatch(
                    existence
                            .stream()
                            .map(
                                    s -> {
                                        DetectionItem detectionItem = new DetectionItem();
                                        detectionItem.setName(s);
                                        detectionItem.setModelId(modelId);
                                        return detectionItem;
                                    }
                            )
                            .collect(Collectors.toList())
            );
        }

        List<DetectionItem> list = list(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getModelId, modelId)
                        .in(DetectionItem::getName, existence)
        );

        for(DetectionItem detectionModel : list) {
            collect.put(detectionModel.getName(), detectionModel);
        }

        return collect;
    }
}
