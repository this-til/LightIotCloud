package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.til.light_iot_cloud.data.Detection;
import com.til.light_iot_cloud.data.DetectionKeyframe;
import com.til.light_iot_cloud.service.DetectionService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 检测关键帧查询控制器
 * 
 * 提供检测关键帧相关的数据查询功能，作为 GraphQL DetectionKeyframe 类型的字段解析器。
 * 主要负责检测关键帧与其关联检测结果的查询和解析。
 * 
 * 主要功能：
 * - 检测关键帧的检测结果列表查询
 * - 关联数据的懒加载
 * - 查询性能优化
 * 
 * 数据加载策略：
 * - 优先使用已加载的检测结果列表
 * - 必要时通过关键帧ID查询关联的检测结果
 * - 支持懒加载以提升性能
 * 
 * @author TIL
 */
@Controller
public class DetectionKeyframeController {

    @Resource
    private DetectionService detectionService;

    /**
     * 获取检测关键帧的检测结果列表
     * 
     * 返回指定检测关键帧包含的所有检测结果。首先检查关键帧对象中是否已加载
     * 检测结果列表，如果没有则通过关键帧ID查询相关的检测结果。
     * 
     * 查询策略：
     * 1. 优先返回已缓存的检测结果列表
     * 2. 如果未缓存，通过关键帧ID查询所有相关检测结果
     * 3. 按检测结果ID排序返回
     * 
     * @param detectionKeyframe 检测关键帧对象
     * @return 检测结果列表，包含该关键帧中的所有检测项目
     */
    @SchemaMapping(typeName = "DetectionKeyframe")
    public List<Detection> detections(DetectionKeyframe detectionKeyframe) {
        if (detectionKeyframe.getDetections() != null) {
            return detectionKeyframe.getDetections();
        }

        LambdaQueryWrapper<Detection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Detection::getKeyframeId, detectionKeyframe.getId());

        return detectionService.list(wrapper);
    }
}
