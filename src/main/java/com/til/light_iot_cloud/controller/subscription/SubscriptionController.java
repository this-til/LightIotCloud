package com.til.light_iot_cloud.controller.subscription;

import com.til.light_iot_cloud.component.DeviceConnectionManager;
import com.til.light_iot_cloud.component.SinkEventHolder;
import com.til.light_iot_cloud.context.AuthContext;
import com.til.light_iot_cloud.data.input.OperationCarInput;
import com.til.light_iot_cloud.enums.DeviceType;
import com.til.light_iot_cloud.event.CommandDownEvent;
import com.til.light_iot_cloud.event.UpdateConfigurationEvent;
import com.til.light_iot_cloud.enums.LinkType;
import jakarta.annotation.Resource;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

/**
 * 设备订阅控制器
 * 
 * 专为设备端提供的订阅控制器，处理设备与服务器之间的实时通信。
 * 设备通过 WebSocket 连接订阅服务器下发的配置更新和命令事件，
 * 实现设备的远程控制和配置管理。
 * 
 * 主要功能：
 * - 设备配置更新事件订阅
 * - 设备命令下发事件订阅
 * - 设备身份验证和权限控制
 * 
 * 连接要求：
 * - 必须使用设备 WebSocket 连接
 * - 需要有效的设备身份认证
 * - 只能接收发送给自己的事件
 * 
 * 安全机制：
 * - 验证连接类型为设备 WebSocket
 * - 基于设备ID过滤事件
 * - 确保设备只能接收自己的事件
 * 
 * @author TIL
 */
@Controller
public class SubscriptionController {
    @Resource
    private DeviceConnectionManager deviceConnectionManager;

    @Resource
    private SinkEventHolder sinkEventHolder;

    /**
     * 配置更新事件订阅
     * 
     * 设备订阅服务器下发的配置更新事件，当服务器需要更新设备配置时，
     * 会通过此订阅向设备推送配置更改信息。设备接收到事件后应立即
     * 应用新的配置参数。
     * 
     * 事件内容：
     * - 配置键名和新值
     * - 配置更新时间戳
     * - 目标设备ID
     * 
     * 安全保证：
     * - 只有设备 WebSocket 连接可以订阅
     * - 设备只能接收发送给自己的配置更新
     * 
     * @param authContext 认证上下文，必须是设备 WebSocket 连接
     * @return 配置更新事件流，经过设备ID过滤
     * @throws IllegalArgumentException 如果不是设备 WebSocket 连接
     */
    @SubscriptionMapping
    public Flux<UpdateConfigurationEvent> updateConfigurationEvent(@ContextValue AuthContext authContext) {

        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        Long deviceId = authContext.getDevice().getId();

        return sinkEventHolder
                .getSinks(UpdateConfigurationEvent.class)
                .asFlux()
                .filter(e -> e.getDeviceId().equals(deviceId));
    }

    /**
     * 命令下发事件订阅
     * 
     * 设备订阅服务器下发的控制命令事件，当用户或系统需要控制设备时，
     * 会通过此订阅向设备发送命令。设备接收到命令后应立即执行相应操作。
     * 
     * 命令类型：
     * - 设备控制命令（开关、调节等）
     * - 功能操作命令（检测、录制等）
     * - 系统命令（重启、更新等）
     * 
     * 事件内容：
     * - 命令键和命令值
     * - 命令发送时间戳
     * - 目标设备ID
     * 
     * 安全保证：
     * - 只有设备 WebSocket 连接可以订阅
     * - 设备只能接收发送给自己的命令
     * 
     * @param authContext 认证上下文，必须是设备 WebSocket 连接
     * @return 命令下发事件流，经过设备ID过滤
     * @throws IllegalArgumentException 如果不是设备 WebSocket 连接
     */
    @SubscriptionMapping
    public Flux<CommandDownEvent> commandDownEvent(@ContextValue AuthContext authContext) {
        LinkType linkType = authContext.getLinkType();

        if (linkType != LinkType.DEVICE_WEBSOCKET) {
            throw new IllegalArgumentException("Unsupported link type: " + linkType);
        }

        Long deviceId = authContext.getDevice().getId();

        return sinkEventHolder
                .getSinks(CommandDownEvent.class)
                .asFlux()
                .filter(e -> e.getDeviceId().equals(deviceId));
    }

}
