package com.notification_service.backend.Services.InternalServices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notification_service.backend.Services.InitService;
import org.springframework.stereotype.Service;

@Service public class ObjectMapperServiceImpl implements InitService, ObjectMapperService {
    private static ObjectMapper objectMapper;

    @Override public void initService() {
        objectMapper = new ObjectMapper();
    }
    @Override public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
