package com.til.light_iot_cloud.context;

import com.til.light_iot_cloud.type.ISubscriptionType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.SneakyThrows;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Publisher {
    @Getter
    AuthContext authContext;

    @Getter
    boolean released;

    Map<String, Sinks.Many<?>> subscriptionMap = new ConcurrentHashMap<>();

    public Publisher(AuthContext authContext) {
        this.authContext = authContext;
    }


    @Nullable
    public <D> Sinks.Many<D> getSubscription(ISubscriptionType<D> subscriptionType) {
        //noinspection unchecked
        return (Sinks.Many<D>) subscriptionMap.get(subscriptionType.name());
    }

    public <D> Sinks.Many<D> registerSubscription(ISubscriptionType<D> subscriptionType) {
        Sinks.Many<?> many = subscriptionMap.get(subscriptionType.name());
        if (many != null) {
            many.tryEmitComplete();
        }
        many = Sinks.many().unicast().onBackpressureBuffer();
        subscriptionMap.put(subscriptionType.name(), many);
        //noinspection unchecked
        return (Sinks.Many<D>) many;
    }

    @Nullable
    public <D> Sinks.Many<D> unregisterSubscription(ISubscriptionType<D> subscriptionType) {
        Sinks.Many<?> many = subscriptionMap.remove(subscriptionType.name());
        if (many != null) {
            many.tryEmitComplete();
            //noinspection unchecked
            return (Sinks.Many<D>) many;
        }
        return null;
    }

    public <D> Sinks.Many<D> existSubscription(ISubscriptionType<D> subscriptionType) {
        Sinks.Many<?> many = subscriptionMap.get(subscriptionType.name());
        if (many != null) {
            //noinspection unchecked
            return (Sinks.Many<D>) many;
        }
        return registerSubscription(subscriptionType);
    }

    public <D> void publisher(ISubscriptionType<D> subscriptionType, D d) {
        Sinks.Many<D> subscription = getSubscription(subscriptionType);
        if (subscription != null) {
            subscription.tryEmitNext(d);
        }
    }

    public <D> void ensurePublisher(ISubscriptionType<D> subscriptionType, D d) {
        existSubscription(subscriptionType).tryEmitNext(d);
    }


    @SneakyThrows
    public void release() {
        synchronized (this) {
            if (released) {
                return;
            }
            released = true;
            if (!subscriptionMap.isEmpty()) {
                subscriptionMap.values().forEach(Sinks.Many::tryEmitComplete);
            }
            if (authContext.getWebSocketSession().isOpen()) {
                authContext.getWebSocketSession().close();
            }
        }
    }

}
