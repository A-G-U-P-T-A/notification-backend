package com.notification_service.backend.Services.InternalServices;

import org.quartz.SchedulerException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface NotificationService {
    public void stopNotificationChain(String key, int userId) throws SchedulerException;
    public void generateNotificationsFromFile(String filename, String key, int notificationNumber, int customerNumber, long startTime, ArrayList<Integer> dataPos);
    public List<Integer> generateFirstNotificationsFromFile(String filename, String key, long startTime);
}
