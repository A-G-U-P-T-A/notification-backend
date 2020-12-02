package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Objects.Notification;

import java.util.Map;

public interface TemplateService {
    public String getUpdatedTemplateData(Notification notification, String template) throws NoSuchFieldException, IllegalAccessException;

}
