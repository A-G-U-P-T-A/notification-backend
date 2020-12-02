package com.notification_service.backend.Services.InternalServices;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public interface SchedulerService {

    public Scheduler getScheduler();
    public void startScheduler();
}
