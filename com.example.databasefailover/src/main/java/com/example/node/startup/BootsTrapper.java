package com.example.node.startup;

import java.io.IOException;
import com.example.databasefailover.DatabaseHandler;
import com.example.databasefailover.ElasticSearchConnection;
import com.example.databasefailover.MongoDBConnection;

public class BootsTrapper {

    public static void main(String[] args) {
        MongoDBConnection.getInstance().createConnection();
        ElasticSearchConnection.getClient();
        DatabaseHandler handler = new DatabaseHandler();

        boolean mongoAvailable = MongoDBConnection.getInstance().isAvailable();
        boolean elasticSearchAvailable = ElasticSearchConnection.isAvailable();

        String[] customerIds = {"12345", "67890", "54321", "98765", "11223", "33445", "55667"};
        String[] dataEntries = {
                "Customer 12345 data",
                "Customer 67890 data",
                "Customer 54321 data",
                "Customer 98765 data",
                "Customer 11223 data",
                "Customer 33445 data",
                "Customer 55667 data"
        };

        if (mongoAvailable) {
            System.out.println("MongoDB available");
            for (int i = 0; i < customerIds.length; i++) {
                handler.storeInMongoDB(customerIds[i], dataEntries[i]);
            }
            System.out.println("Data stored in MongoDB");
        } else if (elasticSearchAvailable) {
            try {
                System.out.println("ElasticSearch available");
                for (int i = 0; i < customerIds.length; i++) {
                    handler.storeInElasticSearch(customerIds[i], dataEntries[i]);
                }
                System.out.println("Data stored in ElasticSearch");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Both databases are unavailable");
        }

        // Fetch data examples
//        for (String customerId : customerIds) {
//            String fetchedData = handler.getData(customerId);
//            System.out.println("Fetched data for customer " + customerId + ": " + fetchedData);
//        }

        // Close Elastic search client properly
        ElasticSearchConnection.closeClient();
    }
}



//public class DatabaseHandler implements HttpHandler {
//    @Override
//    public void handle(HttpExchange exchange) throws IOException {
//        if ("POST".equals(exchange.getRequestMethod())) {
//            InputStream requestBody = exchange.getRequestBody();
//            byte[] requestData = readAllBytes(requestBody);
//
//            String data = new String(requestData);
//
//            boolean mongoAvailable = MongoDBConnection.getInstance().isAvailable();
//            boolean elasticSearchAvailable = ElasticSearchConnection.getInstance().isAvailable();
//
//            if (mongoAvailable) {
//                storeInMongoDB(data);