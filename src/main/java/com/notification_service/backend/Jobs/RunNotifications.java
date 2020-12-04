package com.notification_service.backend.Jobs;

import com.notification_service.backend.Objects.Notification;
import com.notification_service.backend.Services.InternalServices.ConfigLoaderServiceImpl;
import com.notification_service.backend.Services.InternalServices.NotificationServiceImpl;
import com.notification_service.backend.Services.InternalServices.ObjectMapperServiceImpl;
import com.notification_service.backend.Services.InternalServices.TemplateServiceImpl;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;


@Component public class RunNotifications implements Job {

    @Autowired private TemplateServiceImpl templateService;
    @Autowired private ConfigLoaderServiceImpl configLoaderService;
    @Autowired private ObjectMapperServiceImpl objectMapperService;
    @Autowired private NotificationServiceImpl notificationService;

    @SneakyThrows @Override public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("============================================================================================================================================================================================================================");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Notification notification = objectMapperService.getObjectMapper().readValue(jobDataMap.get("notification").toString(), Notification.class);

        sendNotification(notification);

        //System.out.println(jobDataMap.get("notificationNumber"));

        int currentNotificationNumber = jobDataMap.getIntValue("notificationNumber");
        long initJobStartTime = jobDataMap.getLongValue("initJobStartTime");
        String filename = jobDataMap.getString("filename");
        String key = jobDataMap.getString("key");
        int customerNumber = jobDataMap.getIntValue("customerNumber");
        ArrayList<Integer>dataPos = (ArrayList<Integer>) jobDataMap.get("dataPos");
        prepareNextNotification(filename, key, currentNotificationNumber, initJobStartTime, customerNumber, dataPos);

        System.out.println("============================================================================================================================================================================================================================");
    }

    private void prepareNextNotification(String filename, String key, int currentNotificationNumber, long initJobStartTime, int customerNumber, ArrayList<Integer>dataPos) {
        if(currentNotificationNumber+1<dataPos.size())
            notificationService.generateNotificationsFromFile(filename, key, currentNotificationNumber+1,  customerNumber, initJobStartTime, dataPos);
    }

    private void sendNotification(Notification notification) throws NoSuchFieldException, IllegalAccessException {
        int templateId = notification.getTemplateId();
        String template =  configLoaderService.getTemplateFromId(templateId);
        String templateUpdate = templateService.getUpdatedTemplateData(notification, template);
        Date runTime = new Date(System.currentTimeMillis());
        String notificationType = notification.getType();
        System.out.println("Notification Runtime: { " + runTime + " } Notification Type: [ " + notificationType + " ] Updated Template: [ " + templateUpdate + " ] (Sent to customer id: " + notification.getCustomer().getId() + " )");
    }
}
