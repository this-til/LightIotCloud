package com.til.light_iot_cloud.component;

import com.til.light_iot_cloud.data.RequestStatistics;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 请求统计管理器
 * 统计HTTP请求、WebSocket连接和消息等信息
 */
@Component
@Slf4j
@Getter
public class RequestStatisticsManager {

    // HTTP请求统计
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong totalErrors = new AtomicLong(0);

    @PostConstruct
    public void init() {
    }

    public void addRequests() {
        totalRequests.addAndGet(1);
    }

    public void addErrors() {
        totalErrors.addAndGet(1);
    }

    public RequestStatistics asRequestStatistics() {
        RequestStatistics requestStatistics = new RequestStatistics();
        requestStatistics.setTotalHttpRequests(totalRequests.get());
        requestStatistics.setTotalHttpErrors(totalErrors.get());
        return requestStatistics;
    }


}
