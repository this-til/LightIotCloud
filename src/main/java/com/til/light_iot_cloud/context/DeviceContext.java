package com.til.light_iot_cloud.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

}
