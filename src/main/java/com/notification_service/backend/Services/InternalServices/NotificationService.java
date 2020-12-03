package com.notification_service.backend.Services.InternalServices;

import org.quartz.SchedulerException;

import java.io.File;

public interface NotificationService {
    public void stopNotificationChain(String key) throws SchedulerException;
    public void generateNotificationsFromFile(String filename, String key, long notificationNumber, long startTime);
}
