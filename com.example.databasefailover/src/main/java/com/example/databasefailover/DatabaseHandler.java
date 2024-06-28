package com.example.databasefailover;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.bson.Document;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class DatabaseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            InputStream requestBody = exchange.getRequestBody();
            byte[] requestData = readAllBytes(requestBody);

            String data = new String(requestData);

            boolean mongoAvailable = MongoDBConnection.getInstance().isAvailable();
            boolean elasticSearchAvailable = ElasticSearchConnection.isAvailable();

            if (mongoAvailable) {
                storeInMongoDB("id_from_request", data);
            } else if (elasticSearchAvailable) {
                storeInElasticSearch("id_from_request", data);
            } else {
                sendResponse(exchange, 503, "Both databases are unavailable");
                return;
            }

            sendResponse(exchange, 200, "Data processed");
        } else if ("GET".equals(exchange.getRequestMethod())) {
            String id = exchange.getRequestURI().getQuery().split("=")[1];
            String data = getData(id);
            if (data != null) {
                sendResponse(exchange, 200, data);
            } else {
                sendResponse(exchange, 404, "Data not found");
            }
        } else {
            sendResponse(exchange, 405, "Method Not Allowed");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public void storeInMongoDB(String id, String data) {
        MongoCollection<Document> collection = MongoDBConnection.getInstance().getDatabase().getCollection("mycollection");
        Document document = new Document("_id", id).append("data", data);
        collection.insertOne(document);
    }

    public void storeInElasticSearch(String id, String data) throws IOException {
        RestHighLevelClient client = ElasticSearchConnection.getClient();
        IndexRequest request = new IndexRequest("myindex")
                .id(id)
                .source("data", data);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
    }

    public String getData(String id) {
        boolean mongoAvailable = MongoDBConnection.getInstance().isAvailable();
        boolean elasticSearchAvailable = ElasticSearchConnection.isAvailable();

        if (mongoAvailable) {
            return getFromMongoDB(id);
        } else if (elasticSearchAvailable) {
            try {
                return getFromElasticSearch(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFromMongoDB(String id) {
        MongoCollection<Document> collection = MongoDBConnection.getInstance().getDatabase().getCollection("mycollection");
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? doc.getString("data") : null;
    }

    private String getFromElasticSearch(String id) throws IOException {
        RestHighLevelClient client = ElasticSearchConnection.getClient();
        GetRequest getRequest = new GetRequest("myindex", id);
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse.isExists() ? getResponse.getSourceAsString() : null;
    }
}


