package com.til.light_iot_cloud.component;

import com.til.light_iot_cloud.event.CarStateReportEvent;
import com.til.light_iot_cloud.event.ISinksEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SinkEventHolder {

    private final Map<Class<? extends ISinksEvent>, Sinks.Many<? extends ISinksEvent>> sinkMap = new ConcurrentHashMap<>();

    public <E extends ISinksEvent> Sinks.Many<E> getSinks(Class<E> clazz) {
        //noinspection unchecked
        return (Sinks.Many<E>) sinkMap.computeIfAbsent(clazz, c -> Sinks.many().multicast().directAllOrNothing());
    }


    public void publishEvent(ISinksEvent sinksEvent) {
        getSinks(sinksEvent.getClass()).tryEmitNext(of(sinksEvent));
    }

    private <E> E of(Object obj) {
        //noinspection unchecked
        return (E) obj;
    }
}
