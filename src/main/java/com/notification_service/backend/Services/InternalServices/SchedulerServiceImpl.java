package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Services.AutoWiringSpringBeanJobFactory;
import com.notification_service.backend.Services.InitService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Service;


@Service public class SchedulerServiceImpl implements InitService, SchedulerService {

    private static Scheduler scheduler = null;
    @Autowired private ApplicationContext applicationContext;

    @Bean public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @Override public void initService() {
        try {
            if(scheduler==null) {
                scheduler =  new StdSchedulerFactory("quartz.properties").getScheduler();
                scheduler.setJobFactory(springBeanJobFactory());
                startScheduler();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
    @Override public Scheduler getScheduler() {
        return scheduler;
    }
    @Override public void startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
