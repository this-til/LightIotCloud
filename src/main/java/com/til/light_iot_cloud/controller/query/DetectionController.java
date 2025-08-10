package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.data.DetectionItem;
import com.til.light_iot_cloud.data.DetectionModel;
import com.til.light_iot_cloud.service.DetectionItemService;
import com.til.light_iot_cloud.service.DetectionModelService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

/**
 * 检测数据查询控制器
 * <p>
 * 提供检测结果相关的数据查询功能，作为 GraphQL Detection 类型的字段解析器。
 * 主要负责检测数据的关联查询，包括检测模型名称和检测项目名称的解析。
 * <p>
 * 主要功能：
 * - 检测模型名称解析
 * - 检测项目名称解析
 * - 关联数据查询优化
 * <p>
 * 数据解析策略：
 * - 优先使用缓存的字段值
 * - 必要时通过关联查询获取
 * - 支持懒加载和性能优化
 * 
 * @author TIL
 */
@Controller
public class DetectionController {

    @Resource
    private DetectionItemService detectionItemService;

    @Resource
    private DetectionModelService detectionModelService;

    /**
     * 获取检测模型名称
     * <p>
     * 解析检测结果对应的模型名称。首先检查检测对象中是否已缓存模型名称，
     * 如果没有则通过关联查询获取。这种策略可以避免不必要的数据库查询。
     * <p>
     * 查询逻辑：
     * 1. 优先返回已缓存的模型名称
     * 2. 通过检测项目ID查找检测项目
     * 3. 通过模型ID查找检测模型
     * 4. 返回模型名称
     * 
     * @param detection 检测结果对象
     * @return 检测模型名称，如果查询失败则返回null
     */
    @SchemaMapping(typeName = "Detection")
    public String model(Detection detection) {
        if (detection.getModel() != null) {
            return detection.getModel();
        }
        DetectionItem item = detectionItemService.getOne(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getId, detection.getItemId())
        );

        if (item == null) {
            return null;
        }

        DetectionModel model = detectionModelService.getOne(
                new LambdaQueryWrapper<DetectionModel>()
                        .eq(DetectionModel::getId, item.getModelId())
        );

        if (model == null) {
            return null;
        }

        return model.getName();

    }

    /**
     * 获取检测项目名称
     * <p>
     * 解析检测结果对应的项目名称。首先检查检测对象中是否已缓存项目名称，
     * 如果没有则通过检测项目ID查询获取。
     * <p>
     * 查询逻辑：
     * 1. 优先返回已缓存的项目名称
     * 2. 通过检测项目ID查找检测项目
     * 3. 返回项目名称
     * 
     * @param detection 检测结果对象
     * @return 检测项目名称，如果查询失败则返回null
     */
    @SchemaMapping(typeName = "Detection")
    public String item(Detection detection) {

        if (detection.getItem() != null) {
            return detection.getItem();
        }

        DetectionItem item = detectionItemService.getOne(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getId, detection.getItemId())
        );

        if (item == null) {
            return null;
        }

        return item.getName();

    }

}
