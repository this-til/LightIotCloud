package com.til.light_iot_cloud.event;

import org.springframework.context.ApplicationEvent;

public interface ISinksEvent {
    default ApplicationEvent asApplicationEvent() {
        return (ApplicationEvent)this;
    }
}
