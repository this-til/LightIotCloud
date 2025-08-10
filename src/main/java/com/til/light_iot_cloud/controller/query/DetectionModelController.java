package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.DetectionItem;
import com.til.light_iot_cloud.data.DetectionModel;
import com.til.light_iot_cloud.service.DetectionItemService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 检测模型查询控制器
 * <p>
 * 提供检测模型相关的数据查询功能，作为 GraphQL DetectionModel 类型的字段解析器。
 * 主要负责检测模型与其关联检测项目的查询和解析。
 * <p>
 * 主要功能：
 * - 检测模型的检测项目列表查询
 * - 模型与项目的关联关系解析
 * - 支持AI检测功能的数据管理
 * <p>
 * 数据关系：
 * - 一个检测模型可以包含多个检测项目
 * - 检测项目定义了模型能够识别的具体对象类型
 * - 支持动态模型和项目管理
 * 
 * @author TIL
 */
@Controller
public class DetectionModelController {
    @Resource
    private DetectionItemService detectionItemService;

    /**
     * 获取检测模型的检测项目列表
     * <p>
     * 查询指定检测模型包含的所有检测项目。检测项目定义了该模型
     * 能够识别和检测的具体对象类型，如人员、车辆、物品等。
     * <p>
     * 用途：
     * - 展示模型的检测能力
     * - 配置检测参数
     * - 解析检测结果时的项目映射
     * 
     * @param detectionModel 检测模型对象
     * @return 检测项目列表，包含该模型支持的所有检测类型
     */
    @SchemaMapping(typeName = "DetectionModel")
    public List<DetectionItem> items(DetectionModel detectionModel) {
        return detectionItemService.list(
                new LambdaQueryWrapper<DetectionItem>()
                        .eq(DetectionItem::getModelId, detectionModel.getId())
        );
    }
}
