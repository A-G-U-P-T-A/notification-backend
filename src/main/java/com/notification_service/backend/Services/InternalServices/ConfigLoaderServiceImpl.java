package com.notification_service.backend.Services.InternalServices;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.notification_service.backend.Objects.Template;
import com.notification_service.backend.Services.DBServices.MongoDBServiceImpl;
import com.notification_service.backend.Services.InitService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service public class ConfigLoaderServiceImpl implements InitService, ConfigLoaderService {

    @Autowired MongoDBServiceImpl mongoDBService;

    private static final List<Template> templates = new ArrayList<Template>();

    @Override public void initService() {
        Bson filter = Filters.ne("id", -1);
        FindIterable<Document> getTemplates = mongoDBService.getData("template", filter);
        for(Document template: getTemplates) {
            templates.add(new Template(template.getInteger("id"), (String) template.get("template")));
        }
    }
    @Override public String getTemplateFromId(int id) {
        Template template = null;
        for(int i=0;i<templates.size();i++) {
            if(templates.get(i).getId()==id) {
                template = templates.get(i);
                break;
            }
        }
        if(template==null)
            return "";
        return template.getTemplate();
    }
    @Override public List<Template>getTemplates() {
        return templates;
    }
}
