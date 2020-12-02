package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Jobs.RunNotifications;
import com.notification_service.backend.Objects.Notification;
import com.notification_service.backend.Services.InitService;
import lombok.SneakyThrows;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Date;
import java.util.Set;

@Service @EnableAutoConfiguration public class NotificationServiceImpl implements InitService, NotificationService {

    @Autowired ObjectMapperServiceImpl objectMapperService;
    @Autowired SchedulerService schedulerService;
    @Autowired private ApplicationContext applicationContext;

    private static final String GROUP = "notification";

    @Override public void initService() {

    }
    @SneakyThrows @Override public void generateNotificationsFromFile(File file, String key) {
        createNewJobWithParameters(file, key);
    }
    @Override public void stopNotificationChain(String key) throws SchedulerException {
        Set<TriggerKey> triggerKeySet = schedulerService.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP+"_"+key));
        if(triggerKeySet!=null||triggerKeySet.size()!=0) {
            System.out.println("Number of messages to be stopped to reach the user: " + triggerKeySet.size());
            for(TriggerKey triggerKey: triggerKeySet) {
                schedulerService.getScheduler().unscheduleJob(triggerKey);
            }
        } else {
            System.out.println("No Notifications Scheduled for the user found");
        }
    }

    private void createNewJobWithParameters(File file, String key) {
        long startTime = System.currentTimeMillis();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String entry = null;
            while ((entry = br.readLine()) != null) {
                long getCurrentTime = System.currentTimeMillis();
                Notification notification = objectMapperService.getObjectMapper().readValue(entry, Notification.class);
                Date date = new Date(startTime+(notification.getSendTime()*60000));
                System.out.println(date);
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("notification", entry);
                JobDetail jobDetail = getNewJob(getCurrentTime, key, jobDataMap);
                Trigger trigger = getNewTrigger(getCurrentTime, key, date);
                schedulerService.getScheduler().scheduleJob(jobDetail, trigger);
            }
        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        } finally {
            try {
                if(br!=null)
                    br.close();
            } catch (IOException e) {
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
