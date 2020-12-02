package com.notification_service.backend.Services.DBServices;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.notification_service.backend.Services.InitService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

@Service public class MongoDBServiceImpl implements InitService, MongoDBService {
    public MongoClient mongoClient;
    private static final String dbName = "notification";

    @Override public void initService() {
        try {
            mongoClient = MongoClients.create(
                    "mongodb+srv://newuser:newuser@cluster0.7l3am.mongodb.net/notification?retryWrites=true&w=majority");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override public long getCount(String collectionName, Bson filter) {
        return mongoClient.getDatabase(dbName).getCollection(collectionName).countDocuments(filter);
    }
    @Override public FindIterable<Document> getData(String collectionName, Bson filter) {
        return mongoClient.getDatabase(dbName).getCollection(collectionName).find(filter);
    }
}
