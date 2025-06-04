package com.til.light_iot_cloud.component;

import com.til.light_iot_cloud.event.DeviceOnlineStateSwitchEvent;
import com.til.light_iot_cloud.event.ISinksEvent;
import com.til.light_iot_cloud.type.ISubscriptionType;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventSinkHolder {

    private final Map<Class<? extends ISinksEvent>, Sinks.Many<? extends ISinksEvent>> sinkMap = new ConcurrentHashMap<>();

    public <E extends ISinksEvent> Sinks.Many<E> getSinks(Class<E> clazz) {
        //noinspection unchecked
        return (Sinks.Many<E>) sinkMap.computeIfAbsent(clazz, c -> Sinks.many().unicast().onBackpressureBuffer());
    }

    @EventListener
    public void onEvent(ISinksEvent event) {
        //noinspection unchecked,rawtypes
        ((Sinks.Many) getSinks(event.getClass())).tryEmitNext(event);
    }

}
