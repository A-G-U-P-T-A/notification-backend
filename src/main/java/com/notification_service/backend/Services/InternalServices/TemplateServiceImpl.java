package com.notification_service.backend.Services.InternalServices;

import com.notification_service.backend.Objects.Notification;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TemplateServiceImpl implements TemplateService {

    //private static final String fieldStart = "\\$\\{";
    //private static final String fieldEnd = "\\}";
    //private static final String regex = fieldStart + "([^}]+)" + fieldEnd;
    private static final String regex = "\\<(.*?)\\>";
    private static final Pattern pattern = Pattern.compile(regex);

    @Override public String getUpdatedTemplateData(Notification notification, String template) throws NoSuchFieldException, IllegalAccessException {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("employee", notification.getEmployee());
        dataMap.put("customer", notification.getCustomer());
        return format(template, dataMap);
    }

    private String format(String format, Map<String, Object> objects) throws IllegalAccessException, NoSuchFieldException {
        Matcher m = pattern.matcher(format);
        String result = format;
        while (m.find()) {
            String[] found = m.group(1).split("\\.");
            Object obj = objects.get(found[0]);
            Field f = obj.getClass().getField(found[1]);
            String newVal = f.get(obj).toString();
            result = result.replaceFirst(regex, newVal);
        }
        return result;
    }

}
