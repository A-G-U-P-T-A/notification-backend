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
import java.util.*;
import java.util.stream.Stream;

@Service @EnableAutoConfiguration public class NotificationServiceImpl implements InitService, NotificationService {

    @Autowired ObjectMapperServiceImpl objectMapperService;
    @Autowired SchedulerService schedulerService;
    @Autowired private ApplicationContext applicationContext;

    private static final String GROUP = "notification";
    private static final String UPLOADED_FOLDER = "C:\\files\\";

    @Override public void initService() {

    }
    @Override public void stopNotificationChain(String key, int userId) throws SchedulerException {
        Set<TriggerKey> triggerKeySet = schedulerService.getScheduler().getTriggerKeys(GroupMatcher.triggerGroupEquals(GROUP+"_"+key+"_"+userId));
        if(triggerKeySet!=null||triggerKeySet.size()!=0) {
            System.out.println("Received message from the user, userId:"+ userId + " stopping existing triggers: " + triggerKeySet.size());
            for(TriggerKey triggerKey: triggerKeySet) {
                schedulerService.getScheduler().unscheduleJob(triggerKey);
            }
        } else {
            System.out.println("No Notifications Scheduled for the user found");
        }
    }
    @Override public List<Integer> generateFirstNotificationsFromFile(String filename, String key, long startTime) {
        Map<Integer, TreeSet<Integer>>userIdMap = getUserIdListFromFile(filename);
        System.out.println(userIdMap);
        for(int userId: userIdMap.keySet()) {
            createNewJobWithParameters(filename, key, 0, userId, startTime,  new ArrayList<Integer>(userIdMap.get(userId)));
        }
        return new ArrayList<Integer>(userIdMap.keySet());
    }
    @Override public void generateNotificationsFromFile(String filename, String key, int notificationNumber, int customerNumber, long startTime, ArrayList<Integer>dataPos) {
        createNewJobWithParameters(filename, key, notificationNumber, customerNumber,  startTime,  dataPos);
    }



    private void createNewJobWithParameters(String filename, String key, int notificationNumber, int customerNumber, long startTime, ArrayList<Integer>dataPos) {
        File file = new File(UPLOADED_FOLDER+filename);
        if(file.exists()) {
            try (Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()))) {
                String currentNotification = lines.skip(dataPos.get(notificationNumber)).findFirst().orElse(null);
                if (currentNotification != null) {
                    long getCurrentTime = System.currentTimeMillis();
                    Notification notification = objectMapperService.getObjectMapper().readValue(currentNotification, Notification.class);
                    Date date = new Date(startTime + (notification.getSendTime() * 60000));
                    JobDataMap jobDataMap = new JobDataMap();
                    jobDataMap.put("notification", currentNotification);
                    jobDataMap.put("filename", filename);
                    jobDataMap.put("initJobStartTime", startTime);
                    jobDataMap.put("notificationNumber", notificationNumber);
                    jobDataMap.put("key", key);
                    jobDataMap.put("customerNumber", customerNumber);
                    jobDataMap.put("dataPos", dataPos);
                    JobDetail jobDetail = getNewJob(getCurrentTime, key+"_"+customerNumber, jobDataMap);
                    Trigger trigger = getNewTrigger(getCurrentTime, key+"_"+customerNumber, date);
                    schedulerService.getScheduler().scheduleJob(jobDetail, trigger);
                }
            } catch (IOException | SchedulerException e) {
                e.printStackTrace();
            }
        }
    }
    private HashMap<Integer, TreeSet<Integer>> getUserIdListFromFile(String filename) {
        File file = new File(UPLOADED_FOLDER+filename);
        HashMap<Integer, TreeSet<Integer>> userIdList = new HashMap<>();
        int pos = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine())!=null) {
                Notification notification = objectMapperService.getObjectMapper().readValue(line, Notification.class);
                int customerId = notification.getCustomer().getId();
                userIdList.computeIfAbsent(customerId, k -> new TreeSet<>());
                userIdList.get(customerId).add(pos);
                pos++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userIdList;
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
