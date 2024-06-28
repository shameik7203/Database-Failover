package com.example.databasefailover;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoException;

public class MongoDBConnection {
	
	private static MongoDBConnection connection = new MongoDBConnection();
	
	private MongoDBConnection() {
		
	}
    private final String CONNECTION_STRING = "mongodb://localhost:27017";
    private final String DATABASE_NAME = "mydatabase";
    private MongoClient mongoClient = null;

    // get instance
    public static synchronized MongoDBConnection getInstance() {
    	if(connection == null)
    		connection = new MongoDBConnection();
    	return connection;
    }
    
    // Initialize the MongoClient
    public void createConnection() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the MongoDatabase
    public MongoDatabase getDatabase() {
        if (mongoClient != null) {
            return mongoClient.getDatabase(DATABASE_NAME);
        }
        return null;
    }

    // Check if the MongoDB is available
    public boolean isAvailable() {
        try {
            if (getDatabase() != null) {
                getDatabase().listCollectionNames().first();
                return true;
            }
        } catch (MongoException e) {
            e.printStackTrace();
        }
        return false;
    }
}



//create connection initialize mongo client retuen mongoclient return mongodatabase
