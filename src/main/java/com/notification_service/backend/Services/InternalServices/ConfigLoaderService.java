package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Objects.Template;

import java.util.List;

public interface ConfigLoaderService {
    public List<Template>getTemplates();
    public String getTemplateFromId(int id);
}
