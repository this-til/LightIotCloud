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
    default Map<String, DetectionItem> ensureExistence(List<String> set, Long modelId) {
        Map<String, DetectionItem> collect = list(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getModelId, modelId)
                        .in(DetectionItem::getName, set)
        )
                .stream()
                .collect(Collectors.toMap(DetectionItem::getName, m -> m));


        List<String> existence = set.stream()
                .filter(s -> !collect.containsKey(s))
                .toList();

        for(String s : existence) {
            DetectionItem detectionItem = new DetectionItem();
            detectionItem.setName(s);
            detectionItem.setModelId(modelId);
            save(detectionItem);
            collect.put(s, detectionItem);
        }

        return collect;
    }
}
