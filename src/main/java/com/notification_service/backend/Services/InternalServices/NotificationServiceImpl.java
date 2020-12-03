package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Jobs.RunNotifications;
import com.notification_service.backend.Objects.Notification;
import com.notification_service.backend.Services.InitService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

@Service @EnableAutoConfiguration public class NotificationServiceImpl implements InitService, NotificationService {

    @Autowired ObjectMapperServiceImpl objectMapperService;
    @Autowired SchedulerService schedulerService;
    @Autowired private ApplicationContext applicationContext;

    private static final String GROUP = "notification";
    private static final String UPLOADED_FOLDER = "C:\\files\\";

    @Override public void initService() {

    }
    @Override public void stopNotificationChain(String key) throws SchedulerException {
        Set<TriggerKey> triggerKeySet = schedulerService.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP+"_"+key));
        if(triggerKeySet!=null||triggerKeySet.size()!=0) {
            System.out.println("Received message from the user, stopping existing triggers: " + triggerKeySet.size());
            for(TriggerKey triggerKey: triggerKeySet) {
                schedulerService.getScheduler().unscheduleJob(triggerKey);
            }
        } else {
            System.out.println("No Notifications Scheduled for the user found");
        }
    }
    @Override public void generateNotificationsFromFile(String filename, String key, long notificationNumber, long startTime) {
        createNewJobWithParameters(filename, key, notificationNumber, startTime);
    }

    private void createNewJobWithParameters(String filename, String key, long notificationNumber, long startTime) {
        File file = new File(UPLOADED_FOLDER+filename);
        if(file.exists()) {
            try (Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()))) {
                String currentNotification = lines.skip(notificationNumber).findFirst().orElse(null);
                if (currentNotification != null) {
                    long getCurrentTime = System.currentTimeMillis();
                    Notification notification = objectMapperService.getObjectMapper().readValue(currentNotification, Notification.class);
                    Date date = new Date(startTime + (notification.getSendTime() * 60000));
                    System.out.println(date);
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("notification", currentNotification);
                    jobDataMap.put("filename", filename);
                    jobDataMap.put("initJobStartTime", startTime);
                    jobDataMap.put("notificationNumber", notificationNumber);
                    jobDataMap.put("key", key);
                    JobDetail jobDetail = getNewJob(getCurrentTime, key, jobDataMap);
                    Trigger trigger = getNewTrigger(getCurrentTime, key, date);
                    schedulerService.getScheduler().scheduleJob(jobDetail, trigger);
                }
            } catch (IOException | SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
    private JobDetail getNewJob(long getCurrentTime, String key, JobDataMap jobDataMap) {
        return JobBuilder
                .newJob(RunNotifications.class)
                .withIdentity("RunNotifications_"+getCurrentTime, GROUP+"_"+key)
                .usingJobData(jobDataMap)
                .build();
    }
    private Trigger getNewTrigger(long getCurrentTime, String key, Date date) {
        return TriggerBuilder
                .newTrigger()
                .withIdentity("TriggerNotification_"+getCurrentTime, GROUP+"_"+key)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .startAt(date)
                .build();
    }
}
