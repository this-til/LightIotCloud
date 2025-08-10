package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.*;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.enums.LinkType;
import com.til.light_iot_cloud.event.*;
import com.til.light_iot_cloud.service.DeviceService;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

/**
 * WebSocket 订阅控制器
 * <p>
 * 本控制器提供基于 GraphQL WebSocket 的实时数据订阅功能，
 * 支持设备状态、数据采集、检测结果等多种实时事件的推送。
 * 所有订阅方法都要求客户端使用 WebSocket 连接，并具备相应的设备访问权限。
 * <p>
 * 主要功能：
 * - 设备在线状态变化推送
 * - 灯光设备实时状态和数据推送  
 * - 车辆设备实时状态推送
 * - 无人机设备实时状态推送
 * - 检测结果实时推送
 * - 报警对话操作事件推送
 * 
 * @author TIL
 */
@Controller
public class WebSubscriptionController {

    @Resource
    private SinkEventHolder sinkEventHolder;

    @Resource
    private DeviceService deviceService;

    /**
     * 测试订阅接口
     * <p>
     * 用于测试 WebSocket 连接和订阅功能是否正常工作。
     * 每隔1秒推送一个递增的数字。
     * 
     * @return 每秒递增的长整型数字流
     */
    @SubscriptionMapping
    public Flux<Long> testSubscription() {
        return Flux.interval(Duration.ofSeconds(1));
    }

    /**
     * 设备在线状态切换事件订阅
     * <p>
     * 订阅全局设备在线状态变化事件，当任何设备的在线状态发生变化时，
     * 都会向订阅者推送相应的状态切换事件。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @return 设备在线状态切换事件流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接
     */
    @SubscriptionMapping
    public Flux<DeviceOnlineStateSwitchEvent> deviceOnlineStateSwitchEvent(@ContextValue AuthContext authContext) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }
        return sinkEventHolder.getSinks(DeviceOnlineStateSwitchEvent.class).asFlux();
    }

    /**
     * 灯光状态报告事件订阅
     * <p>
     * 订阅指定灯光设备的实时状态数据，包括开关状态、亮度、颜色等信息。
     * 只有设备的所有者才能订阅该设备的状态数据。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @param lightId 灯光设备ID
     * @return 灯光状态数据流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接或设备不存在
     */
    @SubscriptionMapping
    public Flux<LightState> lightStateReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }

        return sinkEventHolder.getSinks(LightStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightStateReportEvent::getLightState);

    }

    /**
     * 灯光数据报告事件订阅
     * <p>
     * 订阅指定灯光设备的实时数据采集信息，包括环境监测数据、传感器读数等。
     * 只有设备的所有者才能订阅该设备的数据。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @param lightId 灯光设备ID
     * @return 灯光数据流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接或设备不存在
     */
    @SubscriptionMapping
    public Flux<LightData> lightDataReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }


        return sinkEventHolder.getSinks(LightDataReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightDataReportEvent::getLightData);

    }

    /**
     * 灯光检测报告事件订阅
     * <p>
     * 订阅指定灯光设备的实时检测结果，包括物体识别、异常检测等关键帧数据。
     * 只有设备的所有者才能订阅该设备的检测数据。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @param lightId 灯光设备ID
     * @return 检测关键帧数据流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接或设备不存在
     */
    @SubscriptionMapping
    public Flux<DetectionKeyframe> lightDetectionReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {

        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }

        return sinkEventHolder.getSinks(LightDetectionReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightDetectionReportEvent::getDetectionKeyframe);

    }

    /**
     * 灯光持续检测报告事件订阅
     * <p>
     * 订阅指定灯光设备的持续检测结果，返回连续的检测项目列表。
     * 适用于需要持续监控和分析的场景。
     * 只有设备的所有者才能订阅该设备的持续检测数据。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @param lightId 灯光设备ID
     * @return 检测项目列表数据流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接或设备不存在
     */
    @SubscriptionMapping
    public Flux<List<Detection>> lightSustainedDetectionReportEvent(@ContextValue AuthContext authContext, @Argument Long lightId) {

        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), lightId, DeviceType.LIGHT);

        if (device == null) {
            throw new IllegalArgumentException("No such light");
        }

        return sinkEventHolder.getSinks(LightSustainedDetectionReportEvent.class)
                .asFlux()
                .filter(e -> e.getLightId().equals(lightId))
                .map(LightSustainedDetectionReportEvent::getDetections);
    }

    /**
     * 报警对话操作事件订阅
     * <p>
     * 订阅全局报警对话操作事件，包括报警的确认、处理、忽略等操作。
     * 所有具有 WebSocket 连接的用户都可以接收到这些事件。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @return 报警对话操作事件流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接
     */
    @SubscriptionMapping
    public Flux<AlarmDialogueOperateEvent> alarmDialogueOperateEvent(@ContextValue AuthContext authContext) {

        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        return sinkEventHolder.getSinks(AlarmDialogueOperateEvent.class)
                .asFlux();
    }

    /**
     * 车辆状态报告事件订阅
     * <p>
     * 订阅指定车辆设备的实时状态数据，包括位置、速度、方向、电池状态等信息。
     * 只有设备的所有者才能订阅该设备的状态数据。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @param carId 车辆设备ID
     * @return 车辆状态数据流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接或设备不存在
     */
    @SubscriptionMapping
    public Flux<CarState> carStateReportEvent(@ContextValue AuthContext authContext, @Argument Long carId) {
        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), carId, DeviceType.CAR);

        if (device == null) {
            throw new IllegalArgumentException("No such car");
        }

        return sinkEventHolder.getSinks(CarStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getCarId().equals(carId))
                .map(CarStateReportEvent::getCarState);
    }

    /**
     * 无人机状态报告事件订阅
     * <p>
     * 订阅指定无人机设备的实时状态数据，包括位置、姿态、飞行状态、电池状态等信息。
     * 只有设备的所有者才能订阅该设备的状态数据。
     * 
     * @param authContext 认证上下文，必须是 WebSocket 连接
     * @param uavId 无人机设备ID
     * @return 无人机状态数据流
     * @throws IllegalArgumentException 如果不是 WebSocket 连接或设备不存在
     */
    @SubscriptionMapping
    public Flux<UavState> uavStateReportEvent(@ContextValue AuthContext authContext, @Argument Long uavId) {

        if (authContext.getLinkType() != LinkType.WEBSOCKET) {
            throw new IllegalArgumentException("Only websocket links are supported");
        }

        Device device = deviceService.getDeviceById(authContext.getUser().getId(), uavId, DeviceType.UAV);

        if (device == null) {
            throw new IllegalArgumentException("No such uav");
        }

        return sinkEventHolder.getSinks(UavStateReportEvent.class)
                .asFlux()
                .filter(e -> e.getDevice().getId().equals(uavId))
                .map(UavStateReportEvent::getUavState);
    }
}
