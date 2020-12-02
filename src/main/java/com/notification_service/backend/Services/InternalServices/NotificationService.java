package com.notification_service.backend.Services.InternalServices;

import org.quartz.SchedulerException;

import java.io.File;

public interface NotificationService {
    public void generateNotificationsFromFile(File file, String key);
    public void stopNotificationChain(String key) throws SchedulerException;
}
