package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.type.ISubscriptionType;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DeviceContext {

    final Map<String, Publisher> publisherMap = new ConcurrentHashMap<>();

    public void register(Publisher publisher) {
        publisherMap.put(publisher.getAuthContext().getWebSocketSession().getId(), publisher);
    }

    public void unregister(Publisher publisher) {
        publisherMap.remove(publisher.getAuthContext().getWebSocketSession().getId());
    }

    public boolean isEmpty() {
        return publisherMap.isEmpty();
    }

    public <D> List<Sinks.Many<D>> findSubscription(ISubscriptionType<D> subscriptionType) {
        return  publisherMap.values().stream()
                .map(p -> p.getSubscription(subscriptionType))
                .filter(Objects::nonNull)
                .toList();
    }

}
