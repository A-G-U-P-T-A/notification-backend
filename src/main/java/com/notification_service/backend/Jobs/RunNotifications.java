package com.notification_service.backend.Jobs;

import com.notification_service.backend.Objects.Notification;
import com.notification_service.backend.Services.InternalServices.ConfigLoaderServiceImpl;
import com.notification_service.backend.Services.InternalServices.ObjectMapperServiceImpl;
import com.notification_service.backend.Services.InternalServices.TemplateServiceImpl;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component public class RunNotifications implements Job {

    @Autowired private TemplateServiceImpl templateService;
    @Autowired private ConfigLoaderServiceImpl configLoaderService;
    @Autowired private ObjectMapperServiceImpl objectMapperService;

    @SneakyThrows @Override public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("============================================================================================================================================================================================================================");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Notification notification = objectMapperService.getObjectMapper().readValue(jobDataMap.get("notification").toString(), Notification.class);
        sendNotification(notification);
        System.out.println("============================================================================================================================================================================================================================");
    }

    private void sendNotification(Notification notification) throws NoSuchFieldException, IllegalAccessException {
        int templateId = notification.getTemplateId();
        String template =  configLoaderService.getTemplateFromId(templateId);
        String templateUpdate = templateService.getUpdatedTemplateData(notification, template);
        Date runTime = new Date(System.currentTimeMillis());
        String notificationType = notification.getType();
        System.out.println("Notification Runtime: " + runTime + " Notification Type: " + notificationType + " Updated Template: " + templateUpdate);
    }
}
