package com.notification_service.backend.Services.DBServices;

import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.conversions.Bson;

public interface MongoDBService {
    public long getCount(String collectionName, Bson filter);

    public FindIterable<Document> getData(String collectionName, Bson filter);
}
