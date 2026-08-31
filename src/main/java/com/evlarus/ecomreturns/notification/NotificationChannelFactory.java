package com.evlarus.ecomreturns.notification;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class NotificationChannelFactory {

    private final Map<NotificationChannelType, NotificationChannel> channels;

    public NotificationChannelFactory(List<NotificationChannel> channels) {
        this.channels = channels.stream()
                .collect(Collectors.toMap(NotificationChannel::type, Function.identity()));
    }

    public NotificationChannel get(NotificationChannelType type) {
        NotificationChannel channel = channels.get(type);
        if (channel == null) {
            throw new IllegalArgumentException("Нет канала уведомлений для типа: " + type);
        }
        return channel;
    }
}
