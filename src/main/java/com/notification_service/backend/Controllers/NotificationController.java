package com.notification_service.backend.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.notification_service.backend.Services.InternalServices.EncryptionServiceImpl;
import com.notification_service.backend.Services.InternalServices.NotificationServiceImpl;
import com.notification_service.backend.Services.InternalServices.ObjectMapperServiceImpl;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.io.File;
import java.util.List;

@RestController public class NotificationController {

    @Autowired ObjectMapperServiceImpl objectMapperService;
    @Autowired NotificationServiceImpl notificationService;
    @Autowired EncryptionServiceImpl encryptionService;

    private static final String UPLOADED_FOLDER = "C:\\files\\";

    @PostMapping(value = "/createnotification") public @ResponseBody ResponseEntity<JsonNode> createNotifications(@RequestBody String payload) throws JsonProcessingException {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapperService.getObjectMapper().readValue(payload, JsonNode.class);
        } catch (JsonProcessingException e) {
            return ResponseEntity.unprocessableEntity().build();
        }
        String fileName = jsonNode.get("filename").asText();
        String key = encryptionService.getMD5(fileName+System.currentTimeMillis());
        File file = new File(UPLOADED_FOLDER+fileName);
        if(file.exists()) {
            List<Integer>userIdList  = notificationService.generateFirstNotificationsFromFile(fileName, key, System.currentTimeMillis());
            ObjectNode response = objectMapperService.getObjectMapper().createObjectNode();
            response.put("key", key);
            response.put("userIdList", objectMapperService.getObjectMapper().readTree(userIdList.toString()));
            return ResponseEntity.ok().body(response);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping(value = "/stopnotification") public @ResponseBody String stopNotifications(@RequestBody String payload) throws JsonProcessingException {
        ObjectNode keyData = objectMapperService.getObjectMapper().readValue(payload, ObjectNode.class);
        String key = keyData.get("key").asText();
        int userId = keyData.get("userId").asInt();
        try {
            notificationService.stopNotificationChain(key, userId);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return key;
    }

    @PreDestroy public void shutdown() {

    }
}
