package com.til.light_iot_cloud.controller.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.til.light_iot_cloud.component.DeviceRunManager;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.context.DeviceContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.data.input.TimeRange;
import com.til.light_iot_cloud.service.*;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * 灯光设备查询控制器
 * <p>
 * 提供灯光设备相关的数据查询功能，作为 GraphQL Light 类型的字段解析器。
 * 负责灯光设备状态、历史数据、检测结果等信息的查询，
 * 支持时间范围筛选和分页查询等功能。
 * <p>
 * 主要功能：
 * - 灯光设备实时状态查询
 * - 历史数据查询（支持时间范围筛选）
 * - 检测关键帧查询（支持分页和时间筛选）
 * - 检测关键帧统计查询
 * - PM2.5数据字段映射
 * <p>
 * 查询特性：
 * - 支持时间范围筛选
 * - 支持分页查询
 * - 自动排序（时间升序/降序）
 * - 数据量限制保护
 * 
 * @author TIL
 */
@Controller
public class LightController {

    @Resource
    private LightDataService lightDataService;

    @Resource
    private DetectionKeyframeService detectionKeyframeService;

    @Resource
    private DetectionItemService detectionItemService;

    @Resource
    private DetectionModelService detectionModelService;

    @Resource
    private DetectionService detectionService;

    @Resource
    private DeviceRunManager deviceRunManager;

    /**
     * 获取灯光设备实时状态
     * <p>
     * 从设备运行管理器中获取指定灯光设备的当前状态信息，
     * 包括开关状态、亮度、工作模式等实时数据。
     * 
     * @param authContext 认证上下文
     * @param light 灯光设备对象
     * @return 灯光状态对象，如果设备离线或上下文无效则返回null
     */
    @SchemaMapping(typeName = "Light")
    public LightState lightState(@ContextValue AuthContext authContext, Device light) {
        DeviceContext deviceContext = deviceRunManager.getDeviceContext(light.getId());

        if (deviceContext == null) {
            return null;
        }

        if (!(deviceContext instanceof DeviceContext.LightContext lightContext)) {
            return null;
        }

        return lightContext.getLightState();
    }

    /**
     * 获取灯光设备历史数据
     * <p>
     * 查询指定灯光设备的历史传感器数据，包括环境监测数据、
     * 设备运行参数等。支持时间范围筛选，默认按时间升序排列。
     * <p>
     * 查询限制：
     * - 最多返回5000条记录
     * - 按时间升序排列
     * - 支持时间范围筛选
     * 
     * @param light 灯光设备对象
     * @param timeRange 时间范围筛选条件，可选参数
     *                  - 如果为空，查询所有历史数据
     *                  - 如果指定，只返回该时间范围内的数据
     * @return 灯光数据列表，按时间升序排列
     */
    @SchemaMapping(typeName = "Light")
    public List<LightData> datas(Device light, @Argument @Nullable TimeRange timeRange) {

        LambdaQueryWrapper<LightData> listQuery = new LambdaQueryWrapper<>();
        listQuery.eq(LightData::getLightId, light.getId());

        if (timeRange != null) {
            timeRange.standard();
            listQuery.between(
                    LightData::getTime,
                    timeRange.getStart(),
                    timeRange.getEnd()
            );
        }

        listQuery.orderByAsc(LightData::getTime);
        return lightDataService.list(new Page<>(0, 5000), listQuery);
    }

    /**
     * 获取检测关键帧列表
     * <p>
     * 查询指定灯光设备的检测关键帧数据，包括检测图像和结果信息。
     * 支持分页查询和时间范围筛选，默认按时间降序排列（最新的在前）。
     * <p>
     * 查询参数：
     * - 支持分页查询，避免大量数据加载
     * - 支持时间范围筛选
     * - 按时间降序排列
     * 
     * @param light 灯光设备对象
     * @param page 分页参数，可选。如果为空则使用默认分页设置
     * @param timeRange 时间范围筛选条件，可选参数
     * @return 检测关键帧列表，按时间降序排列
     */
    @SchemaMapping(typeName = "Light")
    public List<DetectionKeyframe> detectionKeyframes(
            Device light,
            @Argument @Nullable Page<DetectionKeyframe> page,
            @Argument @Nullable TimeRange timeRange
    ) {

        LambdaQueryWrapper<DetectionKeyframe> listQuery = new LambdaQueryWrapper<>();
        listQuery.eq(DetectionKeyframe::getDeviceId, light.getId());

        if (timeRange != null) {
            timeRange.standard();
            listQuery.between(
                    DetectionKeyframe::getTime,
                    timeRange.getStart(),
                    timeRange.getEnd()
            );
        }

        listQuery.orderByDesc(DetectionKeyframe::getTime);

        if (page == null) {
            page = new Page<>();
        }

        return detectionKeyframeService.page(page, listQuery).getRecords();
    }

    /**
     * 获取检测关键帧总数
     * <p>
     * 统计指定灯光设备的检测关键帧总数量，支持时间范围筛选。
     * 用于分页查询的总数统计和数据概览。
     * 
     * @param light 灯光设备对象
     * @param timeRange 时间范围筛选条件，可选参数
     * @return 检测关键帧总数量
     */
    @SchemaMapping(typeName = "Light")
    public Long detectionKeyframeCount(Device light, @Argument @Nullable TimeRange timeRange) {
        LambdaQueryWrapper<DetectionKeyframe> listQuery = new LambdaQueryWrapper<>();
        listQuery.eq(DetectionKeyframe::getDeviceId, light.getId());

        if (timeRange != null) {
            timeRange.standard();
            listQuery.between(
                    DetectionKeyframe::getTime,
                    timeRange.getStart(),
                    timeRange.getEnd()
            );
        }

        return detectionKeyframeService.count(listQuery);
    }

    /**
     * PM2.5数据字段映射
     * <p>
     * 将灯光数据中的PM2.5字段映射到GraphQL响应中。
     * 解决字段命名不一致的问题（数据库字段pm25 -> GraphQL字段pm2_5）。
     * 
     * @param lightData 灯光数据对象
     * @return PM2.5数值
     */
    @SchemaMapping(typeName = "LightData")
    public Double pm2_5(LightData lightData) {
        return lightData.getPm25();
    }
}

