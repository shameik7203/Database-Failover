package com.example.databasefailover;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;

public class ElasticSearchConnection {
    private static final String HOST = "localhost";
    private static final int PORT = 9200;
    private static RestHighLevelClient client = null;

    public static synchronized RestHighLevelClient getClient() {
        if (client == null) {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(HOST, PORT, "http")
                    )
            );
        }
        return client;
    }

    public static boolean isAvailable() {
        try {
            MainResponse response = client.info(RequestOptions.DEFAULT);
            return response != null;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void closeClient() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client = null;
        }
    }
}

