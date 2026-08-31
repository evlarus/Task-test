package com.evlarus.ecomreturns.notification;

public interface NotificationChannel {

    NotificationChannelType type();

    void send(String recipient, String subject, String message);
}
